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

        if (nodes.size() < 4) throw new MolangRuntimeException("CatmullRomCurve requires at least four nodes to interpolate.");

        int effectiveSize = nodes.size() - 2;
        int p1Index = Math.min(Math.max((int) (normalizedInput * (effectiveSize - 1)), 0), effectiveSize - 2) + 1;
        int p0Index = p1Index - 1;
        int p2Index = p1Index + 1;
        int p3Index = p1Index + 2;

        float p0 = environment.resolve(nodes.get(p0Index));
        float p1 = environment.resolve(nodes.get(p1Index));
        float p2 = environment.resolve(nodes.get(p2Index));
        float p3 = environment.resolve(nodes.get(p3Index));

        float t = (normalizedInput * (effectiveSize - 1)) - (p1Index - 1);

        float result = 0.5f * ((2 * p1) + (-p0 + p2) * t + (2 * p0 - 5 * p1 + 4 * p2 - p3) * t * t + (-p0 + 3 * p1 - 3 * p2 + p3) * t * t * t);

        return result;
    }
}