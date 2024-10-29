package de.tomalbrc.heatwave.component.emitter;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.heatwave.component.ParticleComponent;
import gg.moonflower.molangcompiler.api.MolangExpression;

public class EmitterLifetimeLooping implements ParticleComponent<EmitterLifetimeLooping> {
    @SerializedName("active_time")
    public MolangExpression activeTime = MolangExpression.of(10); // default: 10

    @SerializedName("sleep_time")
    public MolangExpression sleepTime = MolangExpression.ZERO; // default: 0
}
