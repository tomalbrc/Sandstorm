package de.tomalbrc.heatwave.component.emitter;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.heatwave.component.ParticleComponent;
import gg.moonflower.molangcompiler.api.MolangExpression;

public class EmitterLifetimeOnce implements ParticleComponent {
    @SerializedName("active_time")
    public MolangExpression activeTime = MolangExpression.of(10); // default: 10
}
