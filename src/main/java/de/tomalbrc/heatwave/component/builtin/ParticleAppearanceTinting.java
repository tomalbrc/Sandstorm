package de.tomalbrc.heatwave.component.builtin;

import com.google.gson.JsonObject;
import de.tomalbrc.heatwave.component.ParticleComponent;

public record ParticleAppearanceTinting(Config config) implements ParticleComponent<ParticleAppearanceTinting.Config> {
    public static class Config {
        JsonObject color;
    }
}