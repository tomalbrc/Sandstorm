package de.tomalbrc.sandstorm.component.emitter;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.sandstorm.component.ParticleComponent;
import gg.moonflower.molangcompiler.api.MolangExpression;

public class EmitterRateInstant implements ParticleComponent<EmitterRateInstant> {
    @SerializedName("num_particles")
    public MolangExpression numParticles = MolangExpression.of(10); // default: 10
}
