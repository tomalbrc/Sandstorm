package de.tomalbrc.heatwave.component.emitter;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.heatwave.component.ParticleComponent;
import gg.moonflower.molangcompiler.api.MolangExpression;

public class EmitterRateSteady implements ParticleComponent {
    @SerializedName("spawn_rate")
    public MolangExpression spawnRate = MolangExpression.of(1); // default: 1

    @SerializedName("max_particles")
    public MolangExpression maxParticles = MolangExpression.of(50); // default: 50
}
