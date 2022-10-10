package com.dxc.CUBIC.util.graph;

import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.invoke.LambdaInvokerFactory;
import com.dxc.CUBIC.entities.Employee;
import com.dxc.CUBIC.entities.RequestDetails;
import com.dxc.CUBIC.entities.graph.WeightedSkills;
import com.dxc.CUBIC.entities.graph.GraphInput;
import com.dxc.CUBIC.entities.graph.GraphOutput;

public class GraphHelper {

    private GraphOutput graph;

    public GraphHelper(RequestDetails requestDetails) {
        // store the graph from skill graph lambda
        graph = invokeGraphLambda(requestDetails);
    }

    public float calculateSkillPercentage(String[] skills, Employee e) {
        float skillPercentage = 0;

        // for every skill in the demand,
        for (String s : skills) {

            // if the employee has the skill,
            if (e.getSkills().contains(s)) {

                // assign 100% for that single skill
                skillPercentage = skillPercentage + 1f;
            }

            //if the employee doesn't have the skill,
            else {

                //loop through the graph's requested skills
                for (Map.Entry<String, WeightedSkills> entry : graph.getRequestedSkillDict().entrySet()) {
                    
                    //when the graph contains the skill
                    if (entry.getKey().equals(s)) {

                        //create a hashmap of the neighbouring skills
                        HashMap<String, Float> map = entry.getValue().toHashMap();
                        float tmp = 0;
                        
                        //loop through the neighbouring skills
                        for (Map.Entry<String, Float> secondEntry : map.entrySet()) {
                            
                            //set the maximum weight from the neighbouring skills 
                            if (e.getSkills().contains(secondEntry.getKey())) {
                                if (secondEntry.getValue() > tmp)
                                    tmp = secondEntry.getValue();
                            }
                        }
                        skillPercentage = skillPercentage + tmp;
                    }
                }
            }
        }

        return skillPercentage / (float) skills.length;
    }

    private GraphOutput invokeGraphLambda(RequestDetails requestDetails) {

        // create invoker instance
        final GraphService graphService = LambdaInvokerFactory.builder()
                .lambdaClient(AWSLambdaClientBuilder.defaultClient())
                .build(GraphService.class);

        // activate the invoker with input and receive a response
        GraphInput input = new GraphInput();
        input.setRequiredSkills(requestDetails.getSkill());
        return graphService.simpleAlgorithm(input);

    }
}
