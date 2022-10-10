package com.dxc.CUBIC.entities.graph;

import java.util.HashMap;

public class WeightedSkills {
    private String[] neighboringSkills;
    private Float[] neighboringPercent;

    public String[] getNeighboringSkills() {
        return neighboringSkills;
    }

    public void setNeighboringSkills(String[] value) {
        neighboringSkills = value;
    }

    public Float[] getNeighboringPercent() {
        return neighboringPercent;
    }

    public void setNeighboringPercent(Float[] value) {
        neighboringPercent = value;
    }

    public HashMap<String, Float> toHashMap() {
        HashMap<String, Float> zipper = new HashMap<String, Float>();
        for (int i = 0; i < this.neighboringSkills.length; i++) {
            zipper.put(neighboringSkills[i], neighboringPercent[i]);
        }
        return zipper;
    }

    public String toString() {
        return this.toHashMap().toString();
    }
}
