package de.tomalbrc.sandstorm.component.particle;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.sandstorm.component.ParticleComponent;
import gg.moonflower.molangcompiler.api.MolangExpression;

public class ParticleInitialSpin implements ParticleComponent<ParticleInitialSpin> {
    @SerializedName("rotation")
    public MolangExpression rotation = MolangExpression.ZERO;
    @SerializedName("rotation_rate")
    public MolangExpression rotationRate = MolangExpression.ZERO;
}
