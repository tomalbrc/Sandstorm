package de.tomalbrc.heatwave.component.particle;

import com.google.common.collect.ImmutableList;
import com.google.gson.annotations.SerializedName;
import de.tomalbrc.heatwave.component.ParticleComponent;
import gg.moonflower.molangcompiler.api.MolangExpression;

import java.util.List;

public class ParticleMotionDynamic implements ParticleComponent<ParticleMotionDynamic> {
    @SerializedName("linear_acceleration")
    public List<MolangExpression> linearAcceleration = ImmutableList.of(MolangExpression.ZERO, MolangExpression.ZERO, MolangExpression.ZERO);
    @SerializedName("linear_drag_coefficient")
    public MolangExpression linearDragCoefficient = MolangExpression.ZERO;
    @SerializedName("rotation_acceleration")
    public MolangExpression rotationAcceleration = MolangExpression.ZERO;
    @SerializedName("rotation_drag_coefficient")
    public MolangExpression rotationDragCoefficient = MolangExpression.ZERO;
}
