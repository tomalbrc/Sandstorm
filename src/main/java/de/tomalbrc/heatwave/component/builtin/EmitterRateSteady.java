package de.tomalbrc.heatwave.component.builtin;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.heatwave.component.ParticleComponent;

public record EmitterRateSteady(Config config) implements ParticleComponent<EmitterRateSteady.Config> {
    public static class Config {
        @SerializedName("spawn_rate")
        int spawnRate = 100;

        @SerializedName("max_particles")
        int mapParticles = 300;


    }
}