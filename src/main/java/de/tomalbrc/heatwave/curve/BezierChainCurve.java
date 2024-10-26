package de.tomalbrc.heatwave.curve;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import gg.moonflower.molangcompiler.api.MolangEnvironment;
import gg.moonflower.molangcompiler.api.MolangExpression;
import gg.moonflower.molangcompiler.api.exception.MolangRuntimeException;

import java.util.List;
import java.util.Map;

public class BezierChainCurve implements Curve {
    public Map<String, Node> nodes;
    public MolangExpression input;

    @SerializedName("horizontal_range")
    public MolangExpression horizontalRange = MolangExpression.of(1.f);


    @Override
    public float evaluate(MolangEnvironment environment) throws MolangRuntimeException {
        return environment.resolve(this.input);
    }

    public static class Node {
        public float value;
        public float left_value;
        public float right_value;
        public float slope;
        public float left_slope;
        public float right_slope;
    }
}
