package de.tomalbrc.sandstorm.curve;

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
        float resolvedInput = environment.resolve(this.input);
        float resolvedRange = environment.resolve(this.horizontalRange);
        float normalizedInput = resolvedInput / resolvedRange;

        if (nodes.size() != 4) throw new MolangRuntimeException("BezierCurve requires exactly four nodes.");

        float p0 = environment.resolve(nodes.get(0));
        float p1 = environment.resolve(nodes.get(1));
        float p2 = environment.resolve(nodes.get(2));
        float p3 = environment.resolve(nodes.get(3));

        return (float) (Math.pow(1 - normalizedInput, 3) * p0 + 3 * Math.pow(1 - normalizedInput, 2) * normalizedInput * p1 + 3 * (1 - normalizedInput) * normalizedInput * normalizedInput * p2 + normalizedInput * normalizedInput * normalizedInput * p3);
    }
}

