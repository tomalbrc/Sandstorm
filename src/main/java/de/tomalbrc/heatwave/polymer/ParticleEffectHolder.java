package de.tomalbrc.heatwave.polymer;

import de.tomalbrc.heatwave.component.ParticleComponentHolder;
import de.tomalbrc.heatwave.component.ParticleComponentMap;
import de.tomalbrc.heatwave.component.ParticleComponents;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.elements.VirtualElement;
import gg.moonflower.molangcompiler.api.MolangRuntime;
import gg.moonflower.molangcompiler.api.exception.MolangRuntimeException;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.List;

public class ParticleEffectHolder extends ElementHolder implements ParticleComponentHolder {
    transient final List<ParticleElement> particleElements = new ObjectArrayList<>();

    private final ParticleComponentMap componentMap = new ParticleComponentMap();
    private final MolangRuntime.Builder runtimeBuilder;
    private MolangRuntime runtime;

    private long lastSpawnTime;
    private short lastSpawnAmount;

    public ParticleEffectHolder() {
        this.runtimeBuilder = MolangRuntime.runtime();
        this.runtime = runtimeBuilder.create();
    }

    @Override
    public void tick() {
        try {
            this.customTick();
        } catch (MolangRuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public void customTick() throws MolangRuntimeException {
        var maxParticles = this.get(ParticleComponents.EMITTER_RATE_STEADY).maxParticles.get(runtime);
        var spawnRate = this.get(ParticleComponents.EMITTER_RATE_STEADY).spawnRate.get(runtime);

        long currentTime = System.currentTimeMillis();
        long timeSinceLastSpawn = currentTime - lastSpawnTime;

        int particlesToSpawn = (int) (timeSinceLastSpawn / (1000.0 / spawnRate));
        particlesToSpawn = (int) Math.min(particlesToSpawn, maxParticles - lastSpawnAmount);

        for (int i = 0; i < particlesToSpawn; i++) {
            this.addElement(new ParticleElement(this));
        }

        // Update the last spawn time
        if (particlesToSpawn > 0) {
            lastSpawnTime = currentTime;
        }

        // Remove old particles
        for (int i = this.particleElements.size() - 1; i >= 0; i--) {
            ParticleElement particleElement = this.particleElements.get(i);
            if (particleElement.isRemoved()) {
                this.removeElement(particleElement);
                lastSpawnAmount--;
            }
        }
    }

    @Override
    public <T extends VirtualElement> T addElement(T element) {
        if (element instanceof ParticleElement particleElement)
            this.particleElements.add(particleElement);
        return super.addElement(element);
    }

    @Override
    public ParticleComponentMap components() {
        return this.componentMap;
    }
}
