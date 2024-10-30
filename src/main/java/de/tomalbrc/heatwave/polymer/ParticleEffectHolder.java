package de.tomalbrc.heatwave.polymer;

import de.tomalbrc.heatwave.Heatwave;
import de.tomalbrc.heatwave.component.ParticleComponentHolder;
import de.tomalbrc.heatwave.component.ParticleComponentMap;
import de.tomalbrc.heatwave.component.ParticleComponents;
import de.tomalbrc.heatwave.curve.Curve;
import de.tomalbrc.heatwave.io.ParticleEffectFile;
import de.tomalbrc.heatwave.util.ShapeUtil;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.elements.VirtualElement;
import gg.moonflower.molangcompiler.api.MolangRuntime;
import gg.moonflower.molangcompiler.api.exception.MolangRuntimeException;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ParticleEffectHolder extends ElementHolder implements ParticleComponentHolder {
    private final static ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    transient final List<ParticleElement> particleElements = new ObjectArrayList<>();

    @NotNull
    private final ParticleEffectFile effectFile;

    private final ParticleComponentMap componentMap = new ParticleComponentMap();
    private final MolangRuntime runtime;

    private int age;
    private boolean canEmit = true;

    private final ServerLevel serverLevel;

    public ParticleEffectHolder(ParticleEffectFile effectFile, ServerLevel level) throws MolangRuntimeException {
        Heatwave.HOLDER.add(this);
        this.serverLevel = level;
        this.effectFile = effectFile;
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

        this.updateRuntimePerEmitter(this.runtime);
        this.initCurves(this.runtime);
    }

    @Override
    public void destroy() {
        Heatwave.HOLDER.remove(this);
        super.destroy();
    }

    private void initCurves(MolangRuntime runtime) {
        var edit = runtime.edit();
        for (Map.Entry<String, Curve> entry : this.effectFile.effect.curves.entrySet()) {
            var key = entry.getKey();
            var val = entry.getValue();

            var index = key.indexOf('.');
            String varName = index != -1 ? key.substring(index + 1) : key;
            edit.setVariable(varName, val::evaluate);
        }
    }

    @NotNull
    public ParticleEffectFile getEffectFile() {
        return this.effectFile;
    }

    public MolangRuntime runtime() {
        return this.runtime;
    }

    private void updateRuntimePerEmitter(MolangRuntime runtime) throws MolangRuntimeException {
        float lifetime = 1;
        var looping = this.get(ParticleComponents.EMITTER_LIFETIME_LOOPING);
        var once = this.get(ParticleComponents.EMITTER_LIFETIME_ONCE);

        if (looping != null) {
            var max = lifetime = runtime.resolve(looping.activeTime);
            var sleepTime = runtime.resolve(looping.sleepTime);
            if (this.age * (1.f/20.f) >= max) {
                // todo: check loop delay
                this.canEmit = false;
                if ((this.age+sleepTime) * (1.f/20.f) >= max) {
                    this.age = 0;
                    this.canEmit = true;
                }
            }
        }
        if (once != null) {
            var max = lifetime = runtime.resolve(once.activeTime);
            if (this.age*(1.f/20.f) >= max) {
                this.canEmit = false;
                this.destroy();
            }
        }

        var rt = runtime.edit();
        rt.setVariable("emitter_age", (float)this.age * (1.f/20.f));
        rt.setVariable("emitter_lifetime", lifetime);
    }

    private boolean canEmit() {
        return this.canEmit;
    }

    private void handlePerUpdateExpression() {
        if (this.has(ParticleComponents.EMITTER_INITIALIZATION) && this.get(ParticleComponents.EMITTER_INITIALIZATION).perUpdateExpression != null) {
            try {
                this.runtime.resolve(this.get(ParticleComponents.EMITTER_INITIALIZATION).perUpdateExpression);
            } catch (MolangRuntimeException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void tick() {
        if (this.getAttachment() == null) {
            return;
        }

        try {
            this.updatePosition();
            this.handlePerUpdateExpression();

            for (int i = 0; i < this.particleElements.size(); i++) {
                this.particleElements.get(i).updateRuntimePerParticle(this.runtime);
                this.particleElements.get(i).tick();
            }

            ParticleEffectHolder.executor.submit(this::asyncTick);

            this.emitOrRemoveParticles();
            this.updateRuntimePerEmitter(this.runtime); // todo: call earlier
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        this.age++;
    }

    private void asyncTick() {
        for (int i = 0; i < this.particleElements.size(); i++) {
            this.particleElements.get(i).asyncTick();
        }
    }

    private void emitOrRemoveParticles() throws MolangRuntimeException {
        var steady = this.get(ParticleComponents.EMITTER_RATE_STEADY);
        var instant = this.get(ParticleComponents.EMITTER_RATE_INSTANT);

        if (steady != null) {
            var maxParticles = runtime.resolve(this.get(ParticleComponents.EMITTER_RATE_STEADY).maxParticles);
            var spawnRate = runtime.resolve(this.get(ParticleComponents.EMITTER_RATE_STEADY).spawnRate) / 20;

            if (this.canEmit()) {
                int particlesToSpawn = (int)spawnRate;
                for (int i = 0; i < particlesToSpawn && this.particleElements.size() <= maxParticles; i++) {
                    this.emit();
                }
            }
        }
        else if (this.age == 0 && instant != null) {
            if (this.canEmit()) {
                int particlesToSpawn = (int) this.runtime.resolve(instant.numParticles);
                for (int i = 0; i < particlesToSpawn; i++) {
                    this.emit();
                }
            }
        }

        for (int i = this.particleElements.size() - 1; i >= 0; i--) {
            ParticleElement particleElement = this.particleElements.get(i);
            if (particleElement.isRemoved()) {
                this.removeElement(particleElement);
            }
        }
    }

    private void emit() throws MolangRuntimeException {
        ParticleElement particle = new ParticleElement(this);
        InitialParticleData particleData = ShapeUtil.initialParticleData(this.runtime, this);
        var pos = this.getPos().add(particleData.offset);
        particle.setPos(pos.x, pos.y, pos.z);
        var initSpeed = this.get(ParticleComponents.PARTICLE_INITIAL_SPEED);
        if (initSpeed != null) {
            var sx = this.runtime.resolve(initSpeed.value().get(0));
            var sy = initSpeed.value().size() > 1 ? this.runtime.resolve(initSpeed.value().get(1)) : sx;
            var sz = initSpeed.value().size() > 1 ? this.runtime.resolve(initSpeed.value().get(2)) : sx;
            particle.setDelta(
                    (float) (particleData.direction.x * sx),
                    (float) (particleData.direction.y * sy),
                    (float) (particleData.direction.z * sz)
            );
        }
        var initSpin = this.get(ParticleComponents.PARTICLE_INITIAL_SPIN);
        if (initSpin != null) {
            particle.setRoll(this.runtime.resolve(initSpin.rotation));
            particle.setRollAccel(this.runtime.resolve(initSpin.rotationRate));
        }
        this.addElement(particle);
    }

    @Override
    public <T extends VirtualElement> T addElement(T element) {
        if (element instanceof ParticleElement particleElement)
            this.particleElements.add(particleElement);
        return super.addElement(element);
    }

    @Override
    public void removeElement(VirtualElement element) {
        super.removeElement(element);
        if (element instanceof ParticleElement particleElement)
            this.particleElements.remove(particleElement);
    }

    @Override
    public ParticleComponentMap components() {
        return this.componentMap;
    }

    public ServerLevel serverLevel() {
        return this.serverLevel;
    }

    public record InitialParticleData(Vec3 offset, Vec3 direction) {
        public static final InitialParticleData ZERO = InitialParticleData.of(Vec3.ZERO, Vec3.ZERO);
        public static InitialParticleData of(Vec3 offset, Vec3 direction) {
            return new InitialParticleData(offset, direction);
        }
    }
}
