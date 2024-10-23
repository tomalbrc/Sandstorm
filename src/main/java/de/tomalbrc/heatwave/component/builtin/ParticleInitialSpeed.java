package de.tomalbrc.heatwave.component.builtin;

import de.tomalbrc.heatwave.component.ParticleComponent;
import org.jetbrains.annotations.NotNull;

public class ParticleInitialSpeed implements ParticleComponent<Double> {
    private Double value;

    @Override
    @NotNull
    public Double config() {
        return this.value;
    }
}
