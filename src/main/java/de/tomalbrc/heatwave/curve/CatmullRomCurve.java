package de.tomalbrc.heatwave.curve;

import com.google.gson.annotations.SerializedName;
import gg.moonflower.molangcompiler.api.MolangEnvironment;
import gg.moonflower.molangcompiler.api.MolangExpression;
import gg.moonflower.molangcompiler.api.exception.MolangRuntimeException;

import java.util.List;

public class CatmullRomCurve implements Curve {
    public List<MolangExpression> nodes;
    public MolangExpression input;

    @SerializedName("horizontal_range")
    public MolangExpression horizontalRange = MolangExpression.of(1.f);

    public float evaluate(MolangEnvironment environment) throws MolangRuntimeException {
        float resolvedInput = environment.resolve(this.input);
        float resolvedRange = environment.resolve(this.horizontalRange);
        float normalizedInput = resolvedInput / resolvedRange;

        if (this.nodes.size() < 4)
            throw new MolangRuntimeException("CatmullRomCurve requires at least 4 nodes!");

        int nodeCount = this.nodes.size();
        float position = normalizedInput * (nodeCount - 1);
        int index = Math.min((int) position, nodeCount - 2);  // Select the index for the second point (P1)

        int p0Index = Math.max(0, index - 1);
        int p2Index = Math.min(index + 1, nodeCount - 1);
        int p3Index = Math.min(index + 2, nodeCount - 1);

        float p0 = environment.resolve(this.nodes.get(p0Index));
        float p1 = environment.resolve(this.nodes.get(index));
        float p2 = environment.resolve(this.nodes.get(p2Index));
        float p3 = environment.resolve(this.nodes.get(p3Index));

        float t = position - index;

        return 0.5f * ((2 * p1) + (-p0 + p2) * t + (2 * p0 - 5 * p1 + 4 * p2 - p3) * t * t + (-p0 + 3 * p1 - 3 * p2 + p3) * t * t * t);
    }
}
