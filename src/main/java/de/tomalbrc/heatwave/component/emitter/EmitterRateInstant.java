package de.tomalbrc.heatwave.component.emitter;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.heatwave.component.ParticleComponent;
import gg.moonflower.molangcompiler.api.MolangExpression;

public class EmitterRateInstant implements ParticleComponent {
    @SerializedName("num_particles")
    public MolangExpression numParticles = MolangExpression.of(10); // default: 10
}
