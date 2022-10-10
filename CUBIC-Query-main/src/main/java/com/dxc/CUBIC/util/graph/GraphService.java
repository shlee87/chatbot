package com.dxc.CUBIC.util.graph;

import com.amazonaws.services.lambda.invoke.LambdaFunction;
import com.dxc.CUBIC.entities.graph.GraphInput;
import com.dxc.CUBIC.entities.graph.GraphOutput;

public interface GraphService {
    @LambdaFunction(functionName = "simple-algorithm-skill-percent-match")
    GraphOutput simpleAlgorithm(GraphInput input);
}
