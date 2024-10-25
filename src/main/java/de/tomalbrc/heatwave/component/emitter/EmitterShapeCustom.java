package de.tomalbrc.heatwave.component.emitter;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.heatwave.component.ParticleComponent;

public class EmitterShapeCustom implements ParticleComponent {
    @SerializedName("offset")
    public float[] offset = new float[]{0.0f, 0.0f, 0.0f}; // default: [0, 0, 0]

    @SerializedName("direction")
    public float[] direction = new float[]{0.0f, 0.0f, 0.0f}; // default: [0, 0, 0]
}
