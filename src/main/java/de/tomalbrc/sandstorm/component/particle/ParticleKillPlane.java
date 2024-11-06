package de.tomalbrc.sandstorm.component.particle;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.sandstorm.component.ParticleComponent;

public class ParticleKillPlane implements ParticleComponent<ParticleKillPlane> {
    @SerializedName("plane")
    public float[] plane = new float[4];
}
