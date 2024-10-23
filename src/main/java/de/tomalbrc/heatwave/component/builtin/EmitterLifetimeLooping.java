package de.tomalbrc.heatwave.component.builtin;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.heatwave.component.ParticleComponent;

public record EmitterLifetimeLooping(Config config) implements ParticleComponent<EmitterLifetimeLooping.Config> {
    public static class Config {
        @SerializedName("active_time")
        public double activeTime = 1.0;
    }
}