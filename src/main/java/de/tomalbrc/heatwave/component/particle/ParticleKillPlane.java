package de.tomalbrc.heatwave.component.particle;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.heatwave.component.ParticleComponent;

public class ParticleKillPlane implements ParticleComponent<ParticleKillPlane> {
    @SerializedName("plane")
    public float[] plane = new float[4];
}
