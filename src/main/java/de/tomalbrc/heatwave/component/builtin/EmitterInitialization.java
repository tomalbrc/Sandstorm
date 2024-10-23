package de.tomalbrc.heatwave.component.builtin;

import de.tomalbrc.heatwave.component.ParticleComponent;

public record EmitterInitialization(Config config) implements ParticleComponent<EmitterInitialization.Config> {
    public static class Config {

    }
}
