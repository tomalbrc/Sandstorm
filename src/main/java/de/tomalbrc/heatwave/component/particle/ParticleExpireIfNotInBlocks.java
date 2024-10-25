package de.tomalbrc.heatwave.component.particle;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.heatwave.component.ParticleComponent;

public class ParticleExpireIfNotInBlocks implements ParticleComponent {
    @SerializedName("blocks")
    public String[] blocks;
}
