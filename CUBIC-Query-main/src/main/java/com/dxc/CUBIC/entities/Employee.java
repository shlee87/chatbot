package com.dxc.CUBIC.entities;

import java.util.Set;

public class Employee {
    private int employeeId;
    private String employeeName;
    private String serviceLine;
    public String workRegion;
    public float percentage;
    public Set<String> skills;
    public int level;
    public String rolloff;
    public String firstAllocation;
    public String levelTitle;

    public String getLevelTitle() {
        return levelTitle;
    }

    public void setLevelTitle(String levelTitle) {
        this.levelTitle = levelTitle;
    }

    public String getFirstAllocation() {
        return firstAllocation;
    }

    public void setFirstAllocation(String firstAllocation) {
        this.firstAllocation = firstAllocation;
    }

    public float getPercentage() {
        return percentage;
    }

    public void setPercentage(float percentage) {
        this.percentage = percentage;
    }

    public Set<String> getSkills() {
        return skills;
    }

    public void setSkills(Set<String> skills) {
        this.skills = skills;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getRolloff() {
        return rolloff;
    }

    public void setRolloff(String rolloff) {
        this.rolloff = rolloff;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public String getServiceLine() {
        return serviceLine;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public void setServiceLine(String serviceLine) {
        this.serviceLine = serviceLine;
    }

    public String getWorkRegion() {
        return workRegion;
    }

    public void setWorkRegion(String workRegion) {
        this.workRegion = workRegion;
    }
}