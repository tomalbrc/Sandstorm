package de.tomalbrc.heatwave.component.particle;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.heatwave.component.ParticleComponent;
import gg.moonflower.molangcompiler.api.MolangExpression;

public class ParticleInitialSpin implements ParticleComponent<ParticleInitialSpin> {
    @SerializedName("rotation")
    public MolangExpression rotation = MolangExpression.of(0);
    @SerializedName("rotation_rate")
    public MolangExpression rotationRate = MolangExpression.of(0);
}
