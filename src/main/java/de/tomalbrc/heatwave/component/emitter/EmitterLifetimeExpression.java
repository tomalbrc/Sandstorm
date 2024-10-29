package de.tomalbrc.heatwave.component.emitter;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.heatwave.component.ParticleComponent;
import gg.moonflower.molangcompiler.api.MolangExpression;

public class EmitterLifetimeExpression implements ParticleComponent<EmitterLifetimeExpression> {
    @SerializedName("activation_expression")
    public MolangExpression activationExpression = MolangExpression.of(1); // default: 1

    @SerializedName("expiration_expression")
    public MolangExpression expirationExpression = MolangExpression.ZERO; // default: 0
}
