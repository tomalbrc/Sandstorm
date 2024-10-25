package de.tomalbrc.heatwave.component.particle;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.heatwave.component.ParticleComponent;

// Particle Initial State Components
public class ParticleInitialSpeed implements ParticleComponent {
    @SerializedName("speed")
    public float speed = 0.0f;
}
