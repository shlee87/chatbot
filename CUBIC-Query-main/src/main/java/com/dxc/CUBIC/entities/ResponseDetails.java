package com.dxc.CUBIC.entities;

import java.util.ArrayList;

public class ResponseDetails {
    private ArrayList<Employee> employees;

    public ResponseDetails(ArrayList<Employee> e) {
        employees = e;
    }

    public ArrayList<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(ArrayList<Employee> employees) {
        this.employees = employees;
    }

}
