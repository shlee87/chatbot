package com.dxc;

import java.sql.*;
import java.util.ArrayList;


import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.dxc.Entities.Employee;
import com.dxc.Entities.RequestDetails;

public class App implements RequestHandler<RequestDetails, ArrayList<Employee>> {

    @Override
    // entry point for the lambda function

    //RequestDetails not needed. Test lambda without RequestDetails once.
    // requestDetails is the POJO representation of the query parameters
    public ArrayList<Employee> handleRequest(RequestDetails requestDetails, Context context) {

        // initialize the list of employees to return through the API
        ArrayList<Employee> employees = new ArrayList<>();

        try {

            // Populate the list of employees
            queryResultToEmployeeList(requestDetails, employees);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        // Return the list of employees
        return employees;
    }

    /**
     * @param requestDetails
     * @param responseDetails
     * @throws SQLException
     */

     //Function where the Employee object gets populated.
    private void queryResultToEmployeeList(RequestDetails requestDetails, ArrayList<Employee> employees)
            throws SQLException {

        // Getting the connection to the database
        Connection conn = getConnection();

        // Creating statement that holds the query and the result after executing the
        // query
        Statement stmt = conn.createStatement();
        System.out.println(stmt);

        // Create the query for the statement by passing the query parameters
        String query = executeQuery(requestDetails);
        ResultSet result = stmt.executeQuery(query);

        // Loop for all the records in the result
        
        while (result.next()) {

            // add a new employee to the employee list
            // by constructing a new employee from the information obtained from the
            // resultset
            employees.add(new Employee(
                //select a.employee_id, employee_name, ppm_roll_off

                ///s.employee_id, e.employee_name, e.job_title, e.job_level, a.ppm_roll_off, s.workers_skill
                    result.getString("employee_id"),
                    result.getString("employee_name"),
                    result.getString("ppm_account_roll_off")));
        }
        
    }

    /**
     * @param request
     * @return
     */
    
    private String executeQuery(RequestDetails request) {

        //initial query string

        //String days = ">90 Days";
        String query = null;
        String roll_off = request.getrollOff();
        //System.out.println("a"+roll_off+"b");
        String[] days = new String[] {"Unassigned", "30 Days"};


        //if request is 30 then returns employees who are "Unassigned" and have a "30 Day" roll off period.
        if (roll_off.equals("30"))
        {
            //System.out.println("b"+roll_off+"b");
            query = "select distinct employee_id, employee_name, ppm_account_roll_off "
                    + "from employees "
                    + "where ppm_account_roll_off IN ( "
                    + "'" + days[0] + "'" + " , "
                    + "'" + days[1] + "'" + ") "
                    + "ORDER BY case when ppm_account_roll_off = "+ "'" + days[0] + "'" +" then 1 "
                    + "when ppm_account_roll_off = "+ "'" + days[1] + "'" +" then 2 "
                    + "end asc;";
        }

        else
        {
            query = "select distinct employee_id, employee_name, ppm_account_roll_off "
                    + "from employees "
                    + "where ppm_account_roll_off LIKE "
                    + "'" + roll_off  + " Days" + "'" + ";";
        }


           
        return query;

    }
    

    /**
     * @return Connection to the database
     * @throws SQLException
     */
    //create a connection to the database
    private Connection getConnection() throws SQLException {

        //Retriving values from Lambda enivironment variables
        
        String hostname = System.getenv("db_endpoint");
        String db_name = System.getenv("db_name");
        String db_user_name = System.getenv("username");
        String db_pwd = System.getenv("password");
        

        Connection conn = DriverManager.getConnection("jdbc:mysql://" + hostname +"/" + db_name, db_user_name, db_pwd);
        return conn;
    }

}