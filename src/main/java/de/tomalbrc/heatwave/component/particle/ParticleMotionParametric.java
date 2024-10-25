package de.tomalbrc.heatwave.component.particle;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.heatwave.component.ParticleComponent;

public class ParticleMotionParametric implements ParticleComponent<ParticleMotionParametric> {
    @SerializedName("relative_position")
    public float[] relativePosition = new float[3];
    @SerializedName("direction")
    public float[] direction = new float[3];
    @SerializedName("rotation")
    public float rotation = 0.0f;
}
