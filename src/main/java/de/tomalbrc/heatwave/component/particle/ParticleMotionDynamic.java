package de.tomalbrc.heatwave.component.particle;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.heatwave.component.ParticleComponent;
import gg.moonflower.molangcompiler.api.MolangExpression;

public class ParticleMotionDynamic implements ParticleComponent<ParticleMotionDynamic> {
    @SerializedName("linear_acceleration")
    public MolangExpression[] linearAcceleration = new MolangExpression[]{MolangExpression.ZERO, MolangExpression.ZERO, MolangExpression.ZERO};
    @SerializedName("linear_drag_coefficient")
    public MolangExpression linearDragCoefficient = MolangExpression.ZERO;
    @SerializedName("rotation_acceleration")
    public MolangExpression rotationAcceleration = MolangExpression.ZERO;
    @SerializedName("rotation_drag_coefficient")
    public MolangExpression rotationDragCoefficient = MolangExpression.ZERO;
}
