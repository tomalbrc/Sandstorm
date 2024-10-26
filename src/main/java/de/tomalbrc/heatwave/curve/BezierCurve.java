package de.tomalbrc.heatwave.curve;

import com.google.gson.annotations.SerializedName;
import gg.moonflower.molangcompiler.api.MolangEnvironment;
import gg.moonflower.molangcompiler.api.MolangExpression;
import gg.moonflower.molangcompiler.api.exception.MolangRuntimeException;

import java.util.List;

public class BezierCurve implements Curve {
    public List<MolangExpression> nodes;
    public MolangExpression input;

    @SerializedName("horizontal_range")
    public MolangExpression horizontalRange = MolangExpression.of(1.f);


    public float evaluate(MolangEnvironment environment) throws MolangRuntimeException {
        return environment.resolve(this.input);
    }
}
