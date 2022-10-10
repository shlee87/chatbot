package com.dxc.CUBIC;

import java.sql.*;
import java.util.ArrayList;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.dxc.CUBIC.entities.Employee;
import com.dxc.CUBIC.entities.RequestDetails;
import com.dxc.CUBIC.entities.ResponseDetails;
import com.dxc.CUBIC.util.QueryHelper;

public class App implements RequestHandler<RequestDetails, ResponseDetails> {

    /**
     * @param requestDetails
     * @param context
     * @return
     */
    @Override
    // entry point for the lambda function
    // requestDetails is the POJO representation of the query parameters
    public ResponseDetails handleRequest(RequestDetails requestDetails, Context context) {

        // initialize the list of employees to return through the API
        ArrayList<Employee> employees = new ArrayList<Employee>();

        try {

            QueryHelper helpMe = new QueryHelper();
            // Populate the list of employees
            helpMe.requestToEmployeeList(requestDetails, employees);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        // Return the list of employees
        return new ResponseDetails(employees);
    }
}
