package de.tomalbrc.sandstorm.component.emitter;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.sandstorm.component.ParticleComponent;
import gg.moonflower.molangcompiler.api.MolangExpression;

public class EmitterRateManual implements ParticleComponent<EmitterRateManual> {
    @SerializedName("max_particles")
    public MolangExpression maxParticles = MolangExpression.of(50); // default: 50
}
