package de.tomalbrc.heatwave.component.emitter;

import com.google.common.collect.ImmutableList;
import com.google.gson.annotations.SerializedName;
import de.tomalbrc.heatwave.component.ParticleComponent;
import gg.moonflower.molangcompiler.api.MolangExpression;

import java.util.List;

public class EmitterShapeCustom implements ParticleComponent<EmitterShapeCustom> {
    @SerializedName("offset")
    public List<MolangExpression> offset = ImmutableList.of(MolangExpression.ZERO, MolangExpression.ZERO, MolangExpression.ZERO); // default: [0, 0, 0]

    @SerializedName("direction")
    public List<MolangExpression> direction = ImmutableList.of(MolangExpression.ZERO, MolangExpression.ZERO, MolangExpression.ZERO); // default: [0, 0, 0]
}
