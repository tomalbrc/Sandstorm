package de.tomalbrc.heatwave.component.particle;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.heatwave.component.ParticleComponent;
import de.tomalbrc.heatwave.component.particle.config.DirectionConfig;


// Particle Appearance Components
public class ParticleAppearanceBillboard implements ParticleComponent {
    @SerializedName("size")
    public float[] size = new float[]{1.0f, 1.0f};
    
    @SerializedName("direction")
    public DirectionConfig direction = new DirectionConfig();
}
