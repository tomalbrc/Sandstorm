package de.tomalbrc.sandstorm.curve;

import com.google.gson.annotations.SerializedName;
import gg.moonflower.molangcompiler.api.MolangEnvironment;
import gg.moonflower.molangcompiler.api.MolangExpression;
import gg.moonflower.molangcompiler.api.exception.MolangRuntimeException;

public class LinearCurve implements Curve {
    public MolangExpression[] nodes;
    public MolangExpression input;

    @SerializedName("horizontal_range")
    public MolangExpression horizontalRange = MolangExpression.of(1.f);

    public float evaluate(MolangEnvironment environment) throws MolangRuntimeException {
        float resolvedInput = environment.resolve(this.input);
        float resolvedRange = environment.resolve(this.horizontalRange);
        float normalizedInput = resolvedInput / resolvedRange;

        if (nodes.length < 2) throw new MolangRuntimeException("LinearCurve requires at least two nodes to interpolate.");

        int leftIndex = Math.min((int) (normalizedInput * (nodes.length - 1)), nodes.length - 2);
        int rightIndex = leftIndex + 1;

        float leftNode = environment.resolve(nodes[leftIndex]);
        float rightNode = environment.resolve(nodes[rightIndex]);

        float t = (normalizedInput * (nodes.length - 1)) - leftIndex;
        return leftNode + t * (rightNode - leftNode);
    }
}
