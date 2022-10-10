package com.dxc.CUBIC.entities;

public class RequestDetails {
    private String[] skill;
    private String rollOff;
    private int jobLevel;
    private boolean exactDate;

    public String[] getSkill() {
        return skill;
    }

    public void setSkill(String[] skill) {
        this.skill = skill;
    }

    public String getRollOff() {
        return rollOff;
    }

    public void setRollOff(String rollOff) {
        this.rollOff = rollOff;
    }

    public int getJobLevel() {
        return jobLevel;
    }

    public void setJobLevel(int jobLevel) {
        this.jobLevel = jobLevel;
    }

    public boolean isExactDate() {
        return exactDate;
    }

    public void setExactDate(boolean exactDate) {
        this.exactDate = exactDate;
    }

}
