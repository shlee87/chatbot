package com.dxc.CUBIC.entities.graph;

import java.util.HashMap;

public class GraphOutput {
    private HashMap<String, WeightedSkills> requestedSkillDict;

    public HashMap<String, WeightedSkills> getRequestedSkillDict() {
        return requestedSkillDict;
    }

    public void setRequestedSkillDict(HashMap<String, WeightedSkills> requestedSkillDict) {
        this.requestedSkillDict = requestedSkillDict;
    }

}
