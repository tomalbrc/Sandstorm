package de.tomalbrc.heatwave.polymer;

import de.tomalbrc.heatwave.Heatwave;
import de.tomalbrc.heatwave.component.ParticleComponent;
import de.tomalbrc.heatwave.component.ParticleComponentType;
import de.tomalbrc.heatwave.component.ParticleComponents;
import de.tomalbrc.heatwave.component.particle.ParticleAppearanceBillboard;
import de.tomalbrc.heatwave.component.particle.ParticleMotionCollision;
import de.tomalbrc.heatwave.component.particle.ParticleMotionDynamic;
import de.tomalbrc.heatwave.component.particle.ParticleMotionParametric;
import de.tomalbrc.heatwave.util.ParticleModels;
import eu.pb4.polymer.virtualentity.api.elements.ItemDisplayElement;
import gg.moonflower.molangcompiler.api.MolangExpression;
import gg.moonflower.molangcompiler.api.MolangRuntime;
import gg.moonflower.molangcompiler.api.exception.MolangRuntimeException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.Brightness;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;

public class ParticleElement extends ItemDisplayElement {
    private static final double MAX_COLLISION_VELOCITY = Mth.square(100.0);

    private float maxLifetime;
    private MolangExpression lifetimeExpression;

    protected boolean removed;
    protected int age;
    protected float roll;
    protected float rolld;

    protected Vector3f previousPosition = new Vector3f();
    protected Vector3f position = new Vector3f();
    protected Vector3f acceleration = new Vector3f();
    protected Vector3f speed = new Vector3f();

    private final boolean physics;
    private final boolean dynamicMotion;
    private final boolean parametricMotion;

    protected float bbRadius = 0.2f;

    protected final float random_1 = (float) Math.random();
    protected final float random_2 = (float) Math.random();
    protected final float random_3 = (float) Math.random();
    protected final float random_4 = (float) Math.random();

    private final ParticleEffectHolder parent;
    private ItemStack item;

    public ParticleElement(ParticleEffectHolder particleEffectHolder) throws MolangRuntimeException {
        this.parent = particleEffectHolder;
        this.physics = parent.has(ParticleComponents.PARTICLE_MOTION_COLLISION);
        this.dynamicMotion = parent.has(ParticleComponents.PARTICLE_MOTION_DYNAMIC);
        this.parametricMotion = parent.has(ParticleComponents.PARTICLE_MOTION_PARAMETRIC);

        if (this.physics) {
            this.bbRadius = this.parent.get(ParticleComponents.PARTICLE_MOTION_COLLISION).collisionRadius;
        }

        this.updateRuntimePerParticle(this.parent.runtime());

        this.item = ParticleModels.modelData(particleEffectHolder.getEffectFile(), 0, this.parent.runtime()).asStack();

        var billboard = this.parent.get(ParticleComponents.PARTICLE_APPEARANCE_BILLBOARD);
        if (billboard != null) {
            var y = billboard.cameraMode == ParticleAppearanceBillboard.CameraMode.LOOKAT_Y || billboard.cameraMode == ParticleAppearanceBillboard.CameraMode.ROTATE_Y;
            if (y) this.setBillboardMode(Display.BillboardConstraints.VERTICAL);
            else this.setBillboardMode(Display.BillboardConstraints.CENTER);
        }

        this.setSendPositionUpdates(true);
        this.setInvisible(true);
        this.setDisplaySize(0.5f, 0.5f);
        this.setInterpolationDuration(2);
        this.setTeleportDuration(1);

        if (!this.parent.has(ParticleComponents.PARTICLE_APPEARANCE_LIGHTING))
            this.setBrightness(Brightness.FULL_BRIGHT);

        if (particleEffectHolder.has(ParticleComponents.PARTICLE_LIFETIME_EXPRESSION)) {
            this.maxLifetime = this.parent.runtime().resolve(this.get(ParticleComponents.PARTICLE_LIFETIME_EXPRESSION).maxLifetime);
            this.lifetimeExpression = particleEffectHolder.get(ParticleComponents.PARTICLE_LIFETIME_EXPRESSION).expirationExpression;
        }

        this.updateElementTick();
    }

    public void setSpeed(float x, float y, float z) {
        this.speed.x = x;
        this.speed.y = y;
        this.speed.z = z;
    }

    public void setRoll(float roll) {
        this.roll = roll;
        this.setLeftRotation(new Quaternionf().rotateZ(this.roll * Mth.DEG_TO_RAD));
    }

    public void setRollAccel(float accel) {
        this.rolld = accel;
    }

    public void updateRuntimePerParticle(MolangRuntime runtime) {
        var rt = runtime.edit();
        rt.setVariable("particle_random_1", this.random_1);
        rt.setVariable("particle_random_2", this.random_2);
        rt.setVariable("particle_random_3", this.random_3);
        rt.setVariable("particle_random_4", this.random_4);

        rt.setVariable("particle_age", this.age * Heatwave.TIME_SCALE);
        rt.setVariable("particle_lifetime", this.maxLifetime);
    }

    @Override
    public Vec3 getCurrentPos() {
        return new Vec3(position.x, position.y+0.05f, position.z);
    }

    private <T extends ParticleComponent<?>> T get(ParticleComponentType<T> type) {
        return this.parent.get(type);
    }

    private boolean alive() throws MolangRuntimeException {
        if (this.parent.getAttachment() == null)
            return false;

        var inBlocks = get(ParticleComponents.PARTICLE_EXPIRE_IF_IN_BLOCKS);
        var notInBlocks = get(ParticleComponents.PARTICLE_EXPIRE_IF_NOT_IN_BLOCKS);

        BlockState bs = inBlocks != null || notInBlocks != null ? this.parent.getAttachment().getWorld().getBlockState(BlockPos.containing(this.position.x, this.position.y, this.position.z)) : null;

        if (inBlocks != null) {
            for (int i = 0; i < inBlocks.value().length; i++) {
                if (inBlocks.value()[i] == bs.getBlock())
                    return false;
            }
        }
        if (notInBlocks != null) {
            boolean alive = false;
            for (int i = 0; i < notInBlocks.value().length; i++) {
                if (notInBlocks.value()[i] == bs.getBlock()) {
                    alive = true;
                    break;
                }
            }
            if (!alive)
                return false;
        }

        if (this.parent.runtime().resolve(this.lifetimeExpression) == 1)
            return false;

        var scaledAge = (float)this.age++ * Heatwave.TIME_SCALE;
        return scaledAge < maxLifetime;
    }

    private float scaledAge() {
        return (float)this.age * Heatwave.TIME_SCALE;
    }

    @Override
    public void tick() {
        try {
            this.previousPosition.set(this.position);

            if (!alive()) {
                this.remove();
                return;
            }

            if (this.parametricMotion) {
                ParticleMotionParametric para = this.parent.get(ParticleComponents.PARTICLE_MOTION_PARAMETRIC);
                this.position.x = this.parent.runtime().resolve(para.relativePosition[0]);
                this.position.y = this.parent.runtime().resolve(para.relativePosition[1]);
                this.position.z = this.parent.runtime().resolve(para.relativePosition[2]);

                if (para.direction.length > 0) {
                    this.speed.x = this.parent.runtime().resolve(para.direction[0]);
                    this.speed.y = this.parent.runtime().resolve(para.direction[1]);
                    this.speed.z = this.parent.runtime().resolve(para.direction[2]);
                }

                this.roll = this.parent.runtime().resolve(para.rotation);
            }

            if (this.dynamicMotion) {
                ParticleMotionDynamic dynMotion = this.parent.get(ParticleComponents.PARTICLE_MOTION_DYNAMIC);
                MolangExpression[] accel = dynMotion.linearAcceleration;
                this.acceleration.x = this.parent.runtime().resolve(accel[0]);
                this.acceleration.y = this.parent.runtime().resolve(accel[1]);
                this.acceleration.z = this.parent.runtime().resolve(accel[2]);

                float dragCoefficient = this.parent.runtime().resolve(dynMotion.linearDragCoefficient);

                this.acceleration.add(this.speed.x * -dragCoefficient, this.speed.y * -dragCoefficient, this.speed.z * -dragCoefficient);
                this.speed.add(this.acceleration.x * Heatwave.TIME_SCALE, this.acceleration.y * Heatwave.TIME_SCALE, this.acceleration.z * Heatwave.TIME_SCALE);
                this.position.add(this.speed.x*Heatwave.TIME_SCALE, this.speed.y*Heatwave.TIME_SCALE, this.speed.z*Heatwave.TIME_SCALE);

                this.move(
                        this.speed.x*Heatwave.TIME_SCALE,
                        this.speed.y*Heatwave.TIME_SCALE,
                        this.speed.z*Heatwave.TIME_SCALE
                );

                float rotationAccel = this.parent.runtime().resolve(dynMotion.rotationAcceleration);
                float rotationDrag = this.parent.runtime().resolve(dynMotion.rotationDragCoefficient);
                rotationAccel -= rotationDrag * this.rolld;
                this.rolld += rotationAccel * Heatwave.TIME_SCALE;

                if (this.rolld != 0) {
                    this.roll += this.rolld * Heatwave.TIME_SCALE;
                }
            }

            this.updateElementTick();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void asyncTick() {
        this.sendPositionUpdates();
        this.sendTrackerUpdates();
    }

    public void setPos(float d, float d2, float d3) {
        this.position.x = d;
        this.position.y = d2;
        this.position.z = d3;
    }

    public void move(double nx, double ny, double nz) {
        if (this.parent.getAttachment() == null || this.parent.getAttachment().getWorld() == null)
            return;

        if (this.physics) {
            if ((nx != 0.0 || ny != 0.0 || nz != 0.0) && nx * nx + ny * ny + nz * nz < MAX_COLLISION_VELOCITY) {
                var bb = this.getBoundingBox();
                Vec3 correctedSpeedFactor = Entity.collideBoundingBox(null, new Vec3(nx, ny, nz), bb, this.parent.getAttachment().getWorld(), List.of());
                boolean xc = correctedSpeedFactor.x != nx;
                boolean yc = correctedSpeedFactor.y != ny;
                boolean zc = correctedSpeedFactor.z != nz;
                boolean collided = xc || yc || zc;
                if (collided) { // coll check
                    ParticleMotionCollision motionCollision = this.get(ParticleComponents.PARTICLE_MOTION_COLLISION);
                    float bounciness = motionCollision.coefficientOfRestitution;

                    if (motionCollision.expireOnContact) {
                        this.remove();
                    }

                    if (xc)
                        this.speed.x *= -1;
                    if (yc)
                        this.speed.y *= -1;
                    if (zc)
                        this.speed.z *= -1;

                    this.position.y = (float) (previousPosition.y + correctedSpeedFactor.y);

                    this.speed.y *= bounciness;
                    this.speed.x = Mth.sign(this.speed.x) * Mth.clamp(Math.abs(this.speed.x) - (motionCollision.collisionDrag * Heatwave.TIME_SCALE), 0, Float.POSITIVE_INFINITY);
                    this.speed.z = Mth.sign(this.speed.z) * Mth.clamp(Math.abs(this.speed.z) - (motionCollision.collisionDrag * Heatwave.TIME_SCALE), 0, Float.POSITIVE_INFINITY);
               }
            }
        }
    }

    private void updateElementTick() throws MolangRuntimeException {
        this.setLeftRotation(new Quaternionf().rotateZ(this.roll * Mth.DEG_TO_RAD));

        if (this.getItem() != this.item) {
            this.item.set(DataComponents.DYED_COLOR, new DyedItemColor(0xFF_FF_FF, false));
            this.setItem(this.item);
        }

        var billboard = this.get(ParticleComponents.PARTICLE_APPEARANCE_BILLBOARD);
        if (billboard != null && !billboard.size.isEmpty()) {
            var size = billboard.size;
            var x = this.parent.runtime().resolve(size.get(0));
            var y = this.parent.runtime().resolve(size.get(1));
            var scale = new Vector3f(Float.isNaN(x) ? 0 : x * 2.f, Float.isNaN(y) ? 0 : y * 2.f, 1);
            if (!this.getScale().equals(scale, 0.0001f)) {
                this.setScale(scale);
            }
        }

        if (billboard != null && billboard.uv.flipbook != null) {
            if (billboard.uv.flipbook.stretch_to_lifetime) {
                float nLifetime = scaledAge() / this.maxLifetime;
                var newStack = ParticleModels.modelData(this.parent.getEffectFile(), nLifetime, this.parent.runtime()).asStack();
                if (this.item.get(DataComponents.CUSTOM_MODEL_DATA) != newStack.get(DataComponents.CUSTOM_MODEL_DATA)) {
                    newStack.set(DataComponents.DYED_COLOR, this.item.get(DataComponents.DYED_COLOR));
                    this.item = newStack;
                    this.setItem(this.item);
                }
            } else {
                var frame = (int)(scaledAge() / (1.f/billboard.uv.flipbook.frames_per_second));
                var max = (int)this.parent.runtime().resolve(billboard.uv.flipbook.max_frame);
                int index = billboard.uv.flipbook.loop ? frame % max : Math.min(frame, max - 1);
                var newStack = ParticleModels.modelData(this.parent.getEffectFile(), index).asStack();
                if (this.item.get(DataComponents.CUSTOM_MODEL_DATA) != newStack.get(DataComponents.CUSTOM_MODEL_DATA)) {
                    newStack.set(DataComponents.DYED_COLOR, this.item.get(DataComponents.DYED_COLOR));
                    this.item = newStack;
                    this.setItem(this.item);
                }
            }
        }

        if (this.parent.has(ParticleComponents.PARTICLE_APPEARANCE_TINTING)) {
            var tinting = this.parent.get(ParticleComponents.PARTICLE_APPEARANCE_TINTING);
            if (tinting.isRGBA()) {
                this.item.set(DataComponents.DYED_COLOR, new DyedItemColor(tinting.rgba(this.parent.runtime()), false));
            }
            else {
                var color = this.parent.get(ParticleComponents.PARTICLE_APPEARANCE_TINTING).color.color(this.parent.runtime());
                this.item.set(DataComponents.DYED_COLOR, new DyedItemColor(color, true));
            }
            this.setItem(this.item);
        }

        this.startInterpolationIfDirty();
    }

    public void remove() {
        this.removed = true;
    }

    public boolean isRemoved() {
        return this.removed;
    }

    public AABB getBoundingBox() {
        var p = previousPosition;

        var nx = p.x - bbRadius;
        var px = p.x + bbRadius;
        double minX = Math.min(nx, px);
        double maxX = Math.max(nx, px);

        double minY = p.y;
        double maxY = p.y + bbRadius;

        var nz = p.z - bbRadius;
        var pz = p.z + bbRadius;
        double minZ = Math.min(nz, pz);
        double maxZ = Math.max(nz, pz);

        return new AABB(minX, minY, minZ, maxX, maxY, maxZ);
    }
}
