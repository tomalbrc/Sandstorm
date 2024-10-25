package de.tomalbrc.heatwave.component.particle;

import de.tomalbrc.heatwave.component.ParticleComponent;

// Particle Initial State Components
public class ParticleInitialSpeed implements ParticleComponent<Integer> {
    public float value = 0.0f;

    public ParticleInitialSpeed(float v) {
        this.value = v;
    }

    @Override
    public Integer value() {
        return ParticleComponent.super.value();
    }
}
