package de.tomalbrc.heatwave.component.particle;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.heatwave.component.ParticleComponent;

public class ParticleMotionDynamic implements ParticleComponent {
    @SerializedName("linear_acceleration")
    public float[] linearAcceleration = new float[3];
    @SerializedName("linear_drag_coefficient")
    public float linearDragCoefficient = 0.0f;
    @SerializedName("rotation_acceleration")
    public float rotationAcceleration = 0.0f;
    @SerializedName("rotation_drag_coefficient")
    public float rotationDragCoefficient = 0.0f;
}
