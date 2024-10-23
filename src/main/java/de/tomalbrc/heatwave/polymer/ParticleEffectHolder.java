package de.tomalbrc.heatwave.polymer;

import de.tomalbrc.heatwave.core.Emitter;
import eu.pb4.polymer.virtualentity.api.ElementHolder;

public class ParticleEffectHolder extends ElementHolder {
    Emitter emitter;

    public ParticleEffectHolder() {
        this.emitter = new Emitter(this);
    }

    protected void onTick() {
        this.emitter.tick();
    }
}
