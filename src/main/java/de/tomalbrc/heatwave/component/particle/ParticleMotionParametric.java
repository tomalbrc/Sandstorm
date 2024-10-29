package de.tomalbrc.heatwave.component.particle;

import com.google.common.collect.ImmutableList;
import com.google.gson.annotations.SerializedName;
import de.tomalbrc.heatwave.component.ParticleComponent;
import gg.moonflower.molangcompiler.api.MolangExpression;

import java.util.List;

public class ParticleMotionParametric implements ParticleComponent<ParticleMotionParametric> {
    @SerializedName("relative_position")
    public List<MolangExpression> relativePosition = ImmutableList.of(MolangExpression.ZERO, MolangExpression.ZERO, MolangExpression.ZERO);
    @SerializedName("direction")
    public List<MolangExpression> direction = ImmutableList.of();
    @SerializedName("rotation")
    public MolangExpression rotation = MolangExpression.ZERO;
}
