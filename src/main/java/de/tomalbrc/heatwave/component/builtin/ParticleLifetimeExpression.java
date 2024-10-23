package de.tomalbrc.heatwave.component.builtin;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.heatwave.component.ParticleComponent;

public record ParticleLifetimeExpression(Config config) implements ParticleComponent<ParticleLifetimeExpression.Config> {
    public static class Config {
        @SerializedName("max_lifetime")
        double maxLifetime;
    }
}