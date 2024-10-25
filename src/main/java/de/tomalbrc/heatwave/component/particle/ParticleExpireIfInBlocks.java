package de.tomalbrc.heatwave.component.particle;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.heatwave.component.ParticleComponent;

// Particle Lifetime Components
public class ParticleExpireIfInBlocks implements ParticleComponent<ParticleExpireIfInBlocks> {
    @SerializedName("blocks")
    public String[] blocks;
}
