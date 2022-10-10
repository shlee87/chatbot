package com.dxc.CUBIC.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import com.dxc.CUBIC.entities.Employee;
import com.dxc.CUBIC.entities.RequestDetails;
import com.dxc.CUBIC.util.graph.GraphHelper;

public class QueryHelper {

    /**
     * @param requestDetails
     * @throws SQLException
     */
    public void requestToEmployeeList(RequestDetails requestDetails, ArrayList<Employee> employees)
            throws SQLException {

        // Getting the connection to the database
        Connection conn = getConnection();

        //change exact date to a 30 60 or 90 format
        if (requestDetails.isExactDate()) {
            changeDateToRollOff(requestDetails);
        }

        //execute the query
        ResultSet result = createQuery(requestDetails, conn);

        //helper to use the graph
        GraphHelper gHelper = new GraphHelper(requestDetails);

        // Loop for all the records in the result
        while (result.next()) {

            Employee e = new Employee();
            e.setEmployeeId(result.getInt("employee_id"));
            e.setEmployeeName(result.getString("employee_name"));
            e.setServiceLine(result.getString("service_line"));
            e.setWorkRegion(result.getString("work_location_region"));
            e.setRolloff(result.getString("ppm_account_roll_off"));
            e.setLevel(result.getInt("job_level"));

            // if the employee has all skills, add the requested skills to the top
            Set<String> skillSet = new LinkedHashSet<String>();
            if (result.getInt("skill_count") == requestDetails.getSkill().length)
                skillSet.addAll(Arrays.asList(requestDetails.getSkill()));
            skillSet.addAll(Arrays.asList(result.getString("skills").split(", ")));

            e.setSkills(skillSet);

            e.setFirstAllocation(result.getString("first_allocation"));
            setTitle(e);
            e.setPercentage(getMatchPercentage(gHelper, requestDetails, result.getInt("skill_count"),
                    e.getLevel(), e));

            employees.add(e);
        }

        //sort the list by their percentage match
        Collections.sort(employees, new EmployeePercentageComparator());
    }

    //set the title of the employee job level
    private void setTitle(Employee e) {
        if (e.getLevel() == 3)
            e.setLevelTitle("Associate");
        else if (e.getLevel() == 4)
            e.setLevelTitle("Professional");
        else if (e.getLevel() == 5)
            e.setLevelTitle("Senior Professional");
        else if (e.getLevel() == 6)
            e.setLevelTitle("Advisor");
        else if (e.getLevel() == 7)
            e.setLevelTitle("Senior Manager");
        else if (e.getLevel() == 1)
            e.setLevelTitle("Assistant");
        else if (e.getLevel() == 2)
            e.setLevelTitle("Senior Assistant");
        else if (e.getLevel() == 8)
            e.setLevelTitle("Director");
        else if (e.getLevel() == 9)
            e.setLevelTitle("Vice President");
        else
            e.setLevelTitle("");
    }

    private void changeDateToRollOff(RequestDetails requestDetails) {
        LocalDate date = LocalDate.parse(requestDetails.getRollOff());
        LocalDate dateNow = LocalDate.now();
        long differenceInDays = dateNow.until(date, ChronoUnit.DAYS);
        int dayDividedThirty = (int) differenceInDays / 30;

        if (dayDividedThirty == 0)
            requestDetails.setRollOff("30");
        else if (dayDividedThirty == 1)
            requestDetails.setRollOff("60");
        else if (dayDividedThirty == 2)
            requestDetails.setRollOff("90");
        else
            requestDetails.setRollOff(">90");
    }

    /**
     * @param request
     * @return
     */
    private ResultSet createQuery(RequestDetails request, Connection conn)
            throws SQLException {
        String[] skillArray = request.getSkill();
        String unknownSkills = new String(new char[skillArray.length]).replace("\0", "?,");
        unknownSkills = unknownSkills.substring(0, unknownSkills.length() - 1);

        String query = "WITH SkillEmployees AS ( "
                + "SELECT "
                + "employee_id  "
                + ",Count(worker_skill) as skill_count "
                + "FROM skills "
                + "WHERE worker_skill IN "
                + "(" + unknownSkills + ") "
                + "GROUP by employee_id "
                + "HAVING COUNT(worker_skill) >= ?"
                + ")  "
                + "SELECT s.employee_id, e.employee_name, e.job_level, "
                + "e.first_allocation, e.work_location_region, e.service_line, e.`ppm_account_roll_off`, se.skill_count "
                + ",GROUP_CONCAT(DISTINCT s.worker_skill SEPARATOR ', ') as skills, e.job_level "
                + "FROM SkillEmployees se "
                + "JOIN employees e on e.employee_id = se.employee_id AND e.last_employee_status='Active'"
                + "JOIN skills s on s.employee_id = se.employee_id "
                + "WHERE e.last_employee_status = 'Active' "
                + "        AND (e.`ppm_account_roll_off`=?) "
                + "       AND e.job_level BETWEEN ? AND ? "
                + "GROUP BY s.employee_id, e.employee_name, e.job_title, e.job_level, e.`ppm_account_roll_off`,  "
                + "e.first_allocation";

        PreparedStatement stmt = conn.prepareStatement(query);
        int index = 1;
        
        //Set the skills from the request to the query
        for (int i = 0; i < skillArray.length; i++) {
            stmt.setString(index, request.getSkill()[i]);
            index++;
        }

        //adjust the number of skills in the employee that can be accepted
        if (skillArray.length > 5)
            stmt.setInt(index, skillArray.length - 3);
        else if (skillArray.length > 3 && skillArray.length < 6)
            stmt.setInt(index, skillArray.length - 2);
        else
            stmt.setInt(index, skillArray.length - 1);

        stmt.setString(index + 1, request.getRollOff() + " Days");
        stmt.setInt(index + 2, request.getJobLevel() - 1);
        stmt.setInt(index + 3, request.getJobLevel());
        return stmt.executeQuery();
    }

    private float getMatchPercentage(GraphHelper gHelper, RequestDetails demand, int skills,
            int level, Employee e) {
        float skillPercentage = 0;
        
        //if employee has all skills, give them 100% for skills
        if (skills == demand.getSkill().length)
            skillPercentage = (1f / 2f);
        else
            skillPercentage = (gHelper.calculateSkillPercentage(demand.getSkill(), e) / 2f);

        int levelDifference = demand.getJobLevel() - level;
        float levelPercentage;
        if (levelDifference == 0)
            levelPercentage = (float) (1f / 2);
        else if (levelDifference == 1)
            levelPercentage = (float) (0.5f / 2);
        else if (levelDifference == -1)
            levelPercentage = (float) (0.25f / 8);
        else
            levelPercentage = (float) (0.25f / 4);
        float percentage = skillPercentage + levelPercentage;

        return percentage;
    }

    /**
     * @return Returns database connection
     * @throws SQLException
     */
    // create a connection to the database
    private Connection getConnection() throws SQLException {
        // Retriving values from Lambda enivironment variables
        String hostname = System.getenv("db_endpoint");
        String dbName = System.getenv("db_name");
        String dbUsername = System.getenv("username");
        String dbPassword = System.getenv("password");

        Connection conn = DriverManager.getConnection("jdbc:mysql://" + hostname + "/" + dbName, dbUsername,
                dbPassword);
        return conn;
    }
}
