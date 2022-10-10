package com.dxc.Entities;

public class Employee {
    private String employee_id;
    private String employee_name;
    private String rollOff;

    public Employee(String e, String n, String p)
    {
        //s.employee_id, e.employee_name, e.job_title, e.job_level, a.ppm_roll_off, s.workers_skill
        employee_id = e;
        employee_name = n;
        rollOff = p;

    }

    public void setemployeeId(String s)
    {
        employee_id = s;
    }
    public void setemployeeName(String j)
    {
        employee_name = j;
    }

    
    public void setrollOff(String t)
    {
        rollOff = t;
    }


    public String getemployeeId()
    {
        return employee_id;
    }

    public String getemployeeName()
    {
        return employee_name;
    }

    public String getrollOff()
    {
        return rollOff;
    }
}