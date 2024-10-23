package de.tomalbrc.heatwave.component;

import org.jetbrains.annotations.NotNull;

public interface ParticleComponent<T> {
    @NotNull
    T config();
}
