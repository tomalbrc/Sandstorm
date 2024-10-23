package de.tomalbrc.heatwave.component.builtin;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.heatwave.component.ParticleComponent;
import gg.moonflower.molangcompiler.api.MolangExpression;

public record EmitterInitialization(Config config) implements ParticleComponent<EmitterInitialization.Config> {
    public static class Config {
        @SerializedName("creation_expression")
        public MolangExpression creationExpression;
    }
}
