package de.tomalbrc.heatwave.component.particle;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.heatwave.component.ParticleComponent;

public class ParticleInitialSpin implements ParticleComponent<ParticleInitialSpin> {
    @SerializedName("rotation")
    public float rotation = 0.0f;
    @SerializedName("rotation_rate")
    public float rotationRate = 0.0f;
}
