
  
package com.dxc.CUBIC;

import java.sql.*;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import com.dxc.CUBIC.entities.RequestDetails;


public class App implements RequestHandler<RequestDetails, JSONObject> {

    @Override
    // entry point for the lambda function
    // requestDetails is the POJO representation of the query parameters
    public JSONObject handleRequest(RequestDetails requestDetails, Context context) {

        // initialize json object of employee to return through the API

        JSONObject jsonResponse = new JSONObject();

        try {

            // Populate the employee object
            queryResultToEmployeeList(requestDetails, jsonResponse);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
      
        //return the employee json object 
        return jsonResponse;
        
    }

 /*  public static void main(String[] args) {
        JSONObject jsonResponse = new JSONObject();

        try {

            // Populate the employee object
            queryResultToEmployeeList(jsonResponse);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        System.out.println(jsonResponse);
    }*/

    /**
     * @param requestDetails The json request is read in through the RequestDetails class
     * @param employee JSON object with employee detail
     * @throws SQLException
     */
    //private static void queryResultToEmployeeList(JSONObject employee)
    private void queryResultToEmployeeList(RequestDetails requestDetails, JSONObject employee)
            throws SQLException {

        // Getting the connection to the database
        Connection conn = getConnection();

        // queries 
        String query = "SELECT * FROM employees_table e WHERE e.employee_id = ?";
        String query2 = "SELECT distinct worker_skill FROM skills_table WHERE employee_id = ?";

        PreparedStatement stmt = conn.prepareStatement(query);
        PreparedStatement stmt2 = conn.prepareStatement(query2);

        //Adding the input employee ID in the prepared statements/ queries
        stmt.setInt(1, requestDetails.getEmployeeID());  

        stmt2.setInt(1, requestDetails.getEmployeeID());

        //stmt.setInt(1, 11544921);
        //stmt2.setInt(1, 11544921);

        // Checkin if the employee with employeeID exists or not
       
        ResultSet result = stmt.executeQuery(); 
        if(!result.isBeforeFirst())
        {
           employee.put("employeeExists", false);
        }
        else{
            employee.put("employeeExists", true);
        ResultSet result2 = stmt2.executeQuery();

        // Loop for all the records in the result
        while (result.next()) {

            // add required informarion of the employee to the employee object
            
        employee.put("employee_id",result.getString("employee_id") );
        employee.put("employee_name",result.getString("employee_name") );
        employee.put("employee_email",result.getString("employee_email") );
        employee.put("employee_type",result.getString("employment_type") );
        employee.put("employee_status",result.getString("employee_status") );
        employee.put("job_level",result.getString("job_level") );
        employee.put("work_city",result.getString("work_location_city") );
        employee.put("work_country",result.getString("work_location_country") );
        employee.put("work_region",result.getString("work_location_region") );
        employee.put("service_line",result.getString("service_line") );
        employee.put("capability",result.getString("capability") );
        employee.put("ppm_roll_off",result.getString("ppm_account_roll_off") );
        employee.put("first_allocation",result.getString("first_allocation") );
       //employee.put("job_family_desc",result.getString("job_family_desc") );
        //employee.put("job_family_group",result.getString("job_family_group") );
        employee.put("job_title",setTitle(result.getInt("job_level"))) ;

        
        }
        //initilizing an json array to store the list of skills
        JSONArray array = new JSONArray();

        // Loop for all the records in the result
        while (result2.next())
        {
            //removing null values 
            if(result2.getString("worker_skill") != null ){
                
            array.add(result2.getString("worker_skill"));
            }
        }
        //adding the skills array to the employee object
        employee.put("skills", array);

        }

        
        
        
    }
    
    //method to get the job title based on job level
    private static String setTitle(int i)
    {
        String [] titleArr = {"No Title","Assistant","Senior Assistant","Associate","Professional","Senior Professional","Advisor", "Senior Manager", "Director","Vice President" };
        return titleArr[i];
    }

    /**
     * @return Returns database connection
     * @throws SQLException
     */
    //create a connection to the database
    private static Connection getConnection() throws SQLException {
        //Retriving values from Lambda enivironment variables
        String hostname = System.getenv("proxy_endpoint");
        String dbName = System.getenv("db_name");
        String dbUsername = System.getenv("username");
        String dbPassword = System.getenv("password");

        //Connection conn = DriverManager.getConnection("jdbc:mysql://ats-data.cghuxbuu9n9j.us-west-2.rds.amazonaws.com/nolaridc_v2"+"?allowMultiQueries=true", "admin","AutomaticTS278!");
        Connection conn = DriverManager.getConnection("jdbc:mysql://"+ hostname + "/"+dbName +"?allowMultiQueries=true", dbUsername,dbPassword);
        return conn;
    }


    

    

}
