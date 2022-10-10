package com.dxc.CUBIC.util;

import java.util.Comparator;

import com.dxc.CUBIC.entities.Employee;

public class EmployeePercentageComparator implements Comparator<Employee> {

    @Override
    public int compare(Employee o1, Employee o2) {
        return (int) ((o2.getPercentage() - o1.getPercentage()) * 100);
    }

}
