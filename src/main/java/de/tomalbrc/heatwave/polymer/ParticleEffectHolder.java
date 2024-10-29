package de.tomalbrc.heatwave.polymer;

import de.tomalbrc.heatwave.component.ParticleComponentHolder;
import de.tomalbrc.heatwave.component.ParticleComponentMap;
import de.tomalbrc.heatwave.component.ParticleComponents;
import de.tomalbrc.heatwave.curve.Curve;
import de.tomalbrc.heatwave.io.ParticleEffectFile;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.elements.VirtualElement;
import gg.moonflower.molangcompiler.api.MolangRuntime;
import gg.moonflower.molangcompiler.api.exception.MolangRuntimeException;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class ParticleEffectHolder extends ElementHolder implements ParticleComponentHolder {
    transient final List<ParticleElement> particleElements = new ObjectArrayList<>();

    @NotNull
    private final ParticleEffectFile effectFile;

    private final ParticleComponentMap componentMap = new ParticleComponentMap();
    private final MolangRuntime runtime;

    private int age;

    public ParticleEffectHolder(ParticleEffectFile effectFile) throws MolangRuntimeException {
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

    public Vec3 particleOffset() throws MolangRuntimeException {
        var point = this.get(ParticleComponents.EMITTER_SHAPE_POINT);
        if (point != null) {
            var x = this.runtime.resolve(point.offset.get(0));
            var y = this.runtime.resolve(point.offset.get(1));
            var z = this.runtime.resolve(point.offset.get(2));
            return this.getPos().add(x,y,z);
        }

        var sphere = this.get(ParticleComponents.EMITTER_SHAPE_SPHERE);
        if (sphere != null) {
            var rad = this.runtime.resolve(sphere.radius);
            var x = this.runtime.resolve(sphere.offset.get(0));
            var y = this.runtime.resolve(sphere.offset.get(1));
            var z = this.runtime.resolve(sphere.offset.get(2));

            float theta = (float) (Math.random() * 2 * Math.PI);
            float phi = (float) Math.acos(2 * Math.random() - 1);
            float dx = (float) (Math.sin(phi) * Math.cos(theta));
            float dy = (float) Math.cos(phi);
            float dz = (float) (Math.sin(phi) * Math.sin(theta));

            return this.getPos()
                    .add(x, y, z)
                    .add(
                            dx * (rad * (sphere.surfaceOnly ? 1 : (float) Math.random())),
                            dy * (rad * (sphere.surfaceOnly ? 1 : (float) Math.random())),
                            dz * (rad * (sphere.surfaceOnly ? 1 : (float) Math.random()))
                    );
        }
        var box = this.get(ParticleComponents.EMITTER_SHAPE_BOX);
        if (box != null) {
            var w = this.runtime.resolve(box.halfDimensions.get(0));
            var h = this.runtime.resolve(box.halfDimensions.get(1));
            var b = this.runtime.resolve(box.halfDimensions.get(2));
            var x = this.runtime.resolve(box.offset.get(0));
            var y = this.runtime.resolve(box.offset.get(1));
            var z = this.runtime.resolve(box.offset.get(2));

            float px, py, pz;
            if (box.surfaceOnly) {
                int face = (int) (Math.random() * 6);
                switch (face) {
                    case 0 -> { px = w; py = (float) (Math.random() * 2 - 1) * h; pz = (float) (Math.random() * 2 - 1) * b; }
                    case 1 -> { px = -w; py = (float) (Math.random() * 2 - 1) * h; pz = (float) (Math.random() * 2 - 1) * b; }
                    case 2 -> { px = (float) (Math.random() * 2 - 1) * w; py = h; pz = (float) (Math.random() * 2 - 1) * b; }
                    case 3 -> { px = (float) (Math.random() * 2 - 1) * w; py = -h; pz = (float) (Math.random() * 2 - 1) * b; }
                    case 4 -> { px = (float) (Math.random() * 2 - 1) * w; py = (float) (Math.random() * 2 - 1) * h; pz = b; }
                    case 5 -> { px = (float) (Math.random() * 2 - 1) * w; py = (float) (Math.random() * 2 - 1) * h; pz = -b; }
                    default -> throw new IllegalStateException("Unexpected face index: " + face);
                }
            } else {
                px = (float) (Math.random() * 2 - 1) * w;
                py = (float) (Math.random() * 2 - 1) * h;
                pz = (float) (Math.random() * 2 - 1) * b;
            }

            return this.getPos().add(x + px, y + py, z + pz);
        }

        var disc = this.get(ParticleComponents.EMITTER_SHAPE_DISC);
        if (disc != null) {
            var r = this.runtime.resolve(disc.radius);
            var nx = this.runtime.resolve(disc.planeNormal.get(0));
            var ny = this.runtime.resolve(disc.planeNormal.get(1));
            var nz = this.runtime.resolve(disc.planeNormal.get(2));
            var x = this.runtime.resolve(disc.offset.get(0));
            var y = this.runtime.resolve(disc.offset.get(1));
            var z = this.runtime.resolve(disc.offset.get(2));

            float angle = (float) (Math.random() * 2 * Math.PI);
            float radius = disc.surfaceOnly ? r : (float) Math.random() * r;

            float dx = (float) (Math.cos(angle) * radius);
            float dy = (float) (Math.sin(angle) * radius);

            // orthogonal vector
            float ux = ny * nz - nx * nz; // cross prod
            float uy = -nx * nz;
            float uz = nx * ny;

            return this.getPos()
                    .add(x, y, z)
                    .add(nx * dx + ux * dy, ny * dx + uy * dy, nz * dx + uz * dy);
        }

        return this.getPos();
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
            if (this.age*(1.f/20.f) >= max) {
                // todo: check loop delay
                this.age = 0;
            }
        }
        if (once != null) {
            var max = lifetime = runtime.resolve(once.activeTime);
            if (this.age*(1.f/20.f) >= max) {
                this.destroy();
            }
        }

        var rt = runtime.edit();
        rt.setVariable("emitter_age", (float)this.age * (1.f/20.f));
        rt.setVariable("emitter_lifetime", lifetime);
    }

    private boolean canEmit() {
        return true;
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

            for (ParticleElement e : this.particleElements) {
                e.updateRuntimePerParticle(this.runtime);
                e.tick();
            }

            this.emitOrRemoveParticles();
            this.updateRuntimePerEmitter(this.runtime); // todo: call earlier
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        this.age++;
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
                    this.addElement(new ParticleElement(this));
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
        this.addElement(new ParticleElement(this));
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
}
