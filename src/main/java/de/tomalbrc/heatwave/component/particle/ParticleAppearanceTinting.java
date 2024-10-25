package de.tomalbrc.heatwave.component.particle;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.heatwave.component.ParticleComponent;
import de.tomalbrc.heatwave.component.particle.config.ColorConfig;

public class ParticleAppearanceTinting implements ParticleComponent {
    @SerializedName("color")
    public ColorConfig color = new ColorConfig();
}
