package de.tomalbrc.heatwave.polymer;

import de.tomalbrc.heatwave.component.ParticleComponentHolder;
import de.tomalbrc.heatwave.component.ParticleComponentMap;
import de.tomalbrc.heatwave.component.ParticleComponents;
import de.tomalbrc.heatwave.io.ParticleEffectFile;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.elements.VirtualElement;
import gg.moonflower.molangcompiler.api.MolangRuntime;
import gg.moonflower.molangcompiler.api.exception.MolangRuntimeException;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.List;

public class ParticleEffectHolder extends ElementHolder implements ParticleComponentHolder {
    transient final List<ParticleElement> particleElements = new ObjectArrayList<>();

    private final ParticleEffectFile effectFile;

    private final ParticleComponentMap componentMap = new ParticleComponentMap();
    private final MolangRuntime runtime;

    private long lastSpawnTime;

    public ParticleEffectHolder(ParticleEffectFile effectFile) {
        this.initComponents(effectFile.effect.components);
        this.runtime = MolangRuntime.runtime().create();
        var edit = this.runtime.edit();
        edit.setVariable("emitter_random_1", (float) Math.random());
        edit.setVariable("emitter_random_2", (float) Math.random());
        edit.setVariable("emitter_random_3", (float) Math.random());
        edit.setVariable("emitter_random_4", (float) Math.random());

        if (this.has(ParticleComponents.EMITTER_INITIALIZATION) && this.get(ParticleComponents.EMITTER_INITIALIZATION).creationExpression != null) {
            try {
                this.runtime.resolve(this.get(ParticleComponents.EMITTER_INITIALIZATION).creationExpression);
            } catch (MolangRuntimeException e) {
                throw new RuntimeException(e);
            }
        }

        this.effectFile = effectFile;
    }

    public ParticleEffectFile getEffectFile() {
        return effectFile;
    }

    public MolangRuntime runtime() {
        return this.runtime;
    }

    @Override
    public void tick() {
        super.tick();
        try {
            if (this.has(ParticleComponents.EMITTER_INITIALIZATION) && this.get(ParticleComponents.EMITTER_INITIALIZATION).perUpdateExpression != null) {
                try {
                    this.runtime.resolve(this.get(ParticleComponents.EMITTER_INITIALIZATION).perUpdateExpression);
                } catch (MolangRuntimeException e) {
                    throw new RuntimeException(e);
                }
            }

            this.customTick();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void customTick() throws MolangRuntimeException {
        var maxParticles = runtime.resolve(this.get(ParticleComponents.EMITTER_RATE_STEADY).maxParticles);
        var spawnRate = runtime.resolve(this.get(ParticleComponents.EMITTER_RATE_STEADY).spawnRate);

        long currentTime = System.currentTimeMillis();
        long timeSinceLastSpawn = currentTime - lastSpawnTime;

        int particlesToSpawn = (int) (timeSinceLastSpawn * (spawnRate/1000.0));
        particlesToSpawn = (int) Math.min(particlesToSpawn, maxParticles - particleElements.size());

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
            }
        }
    }

    @Override
    public <T extends VirtualElement> T addElement(T element) {
        if (element instanceof ParticleElement particleElement)
            this.particleElements.add(particleElement);
        return super.addElement(element);
    }

    public void removeElement(VirtualElement element) {
        super.removeElement(element);
        this.particleElements.remove((ParticleElement) element);
    }

    @Override
    public ParticleComponentMap components() {
        return this.componentMap;
    }
}
