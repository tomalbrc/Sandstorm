package de.tomalbrc.heatwave.component.emitter;

import com.google.common.collect.ImmutableList;
import com.google.gson.annotations.SerializedName;
import de.tomalbrc.heatwave.component.ParticleComponent;
import gg.moonflower.molangcompiler.api.MolangExpression;

import java.util.List;

public class EmitterShapePoint implements ParticleComponent<EmitterShapePoint> {
    @SerializedName("offset")
    public List<MolangExpression> offset = ImmutableList.of(MolangExpression.of(0), MolangExpression.of(0), MolangExpression.of(0)); // default: [0, 0, 0]

    @SerializedName("direction")
    public List<MolangExpression> direction = ImmutableList.of(MolangExpression.of(0), MolangExpression.of(0), MolangExpression.of(0)); // specify direction
}
