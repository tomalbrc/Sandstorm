package de.tomalbrc.heatwave.polymer;

import de.tomalbrc.heatwave.Heatwave;
import de.tomalbrc.heatwave.component.ParticleComponent;
import de.tomalbrc.heatwave.component.ParticleComponentType;
import de.tomalbrc.heatwave.component.ParticleComponents;
import de.tomalbrc.heatwave.util.ParticleModels;
import eu.pb4.polymer.virtualentity.api.elements.ItemDisplayElement;
import eu.pb4.polymer.virtualentity.api.tracker.DisplayTrackedData;
import gg.moonflower.molangcompiler.api.MolangExpression;
import gg.moonflower.molangcompiler.api.MolangRuntime;
import gg.moonflower.molangcompiler.api.exception.MolangRuntimeException;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.util.Brightness;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;

public class ParticleElement extends ItemDisplayElement {
    private static final AABB INITIAL_AABB = new AABB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
    private static final double MAX_COLLISION_VELOCITY = Mth.square(100.0);

    private MolangExpression maxLifetime;
    private MolangExpression lifetimeExpression;
    private AABB bb = INITIAL_AABB;

    protected boolean onGround;
    private boolean stoppedByCollision;
    protected boolean removed;
    protected int age;
    protected float roll;
    protected float rolld;

    protected double xo;
    protected double yo;
    protected double zo;
    protected double x;
    protected double y;
    protected double z;
    protected float xd;
    protected float yd;
    protected float zd;

    private final boolean physics;
    private final boolean dynamicMotion;
    private final boolean parametricMotion;

    protected float bbRadius = 0.2f;

    protected final float random_1 = (float) Math.random();
    protected final float random_2 = (float) Math.random();
    protected final float random_3 = (float) Math.random();
    protected final float random_4 = (float) Math.random();

    private final ParticleEffectHolder parent;
    private final ItemStack item;

    public ParticleElement(ParticleEffectHolder particleEffectHolder) throws MolangRuntimeException {
        this.parent = particleEffectHolder;
        this.physics = parent.has(ParticleComponents.PARTICLE_MOTION_COLLISION);
        this.dynamicMotion = parent.has(ParticleComponents.PARTICLE_MOTION_DYNAMIC);
        this.parametricMotion = parent.has(ParticleComponents.PARTICLE_MOTION_PARAMETRIC);

        if (particleEffectHolder.has(ParticleComponents.PARTICLE_LIFETIME_EXPRESSION)) {
            this.maxLifetime = this.get(ParticleComponents.PARTICLE_LIFETIME_EXPRESSION).maxLifetime;
            this.lifetimeExpression = particleEffectHolder.get(ParticleComponents.PARTICLE_LIFETIME_EXPRESSION).expirationExpression;
        }

        if (this.physics) {
            this.bbRadius = this.parent.get(ParticleComponents.PARTICLE_MOTION_COLLISION).collisionRadius;
        }

        this.updateRuntimePerParticle(this.parent.runtime());

        this.item = ParticleModels.modelData(particleEffectHolder.getEffectFile()).asStack();

        if (this.parent.has(ParticleComponents.PARTICLE_APPEARANCE_BILLBOARD))
            this.setBillboardMode(Display.BillboardConstraints.CENTER);

        this.setSendPositionUpdates(true);
        this.setInvisible(true);
        this.setDisplaySize(0.2f, 0.2f);
        this.setInterpolationDuration(3);
        this.setTeleportDuration(2);

        if (!this.parent.has(ParticleComponents.PARTICLE_APPEARANCE_LIGHTING))
            this.setBrightness(Brightness.FULL_BRIGHT);
    }

    public void setDelta(float x, float y, float z) {
        this.xd = x;
        this.yd = y;
        this.zd = z;
    }

    public void setRoll(float roll) {
        this.roll = roll;
    }

    public void setRollAccel(float accel) {
        this.rolld = accel;
    }

    public void updateRuntimePerParticle(MolangRuntime runtime) throws MolangRuntimeException {
        var rt = runtime.edit();
        rt.setVariable("particle_random_1", this.random_1);
        rt.setVariable("particle_random_2", this.random_2);
        rt.setVariable("particle_random_3", this.random_3);
        rt.setVariable("particle_random_4", this.random_4);

        rt.setVariable("particle_age", this.age * Heatwave.TIME_SCALE);
        rt.setVariable("particle_lifetime", runtime.resolve(this.maxLifetime));
    }

    @Override
    public Vec3 getCurrentPos() {
        return new Vec3(x,y+0.05f,z);
    }

    private <T extends ParticleComponent<?>> T get(ParticleComponentType<T> type) {
        return this.parent.get(type);
    }

    private boolean alive(int age) throws MolangRuntimeException {
        var scaledAge = (float)this.age * Heatwave.TIME_SCALE;
        return this.maxLifetime != null && scaledAge < this.parent.runtime().resolve(this.maxLifetime) || this.lifetimeExpression != null && age < this.parent.runtime().resolve(this.lifetimeExpression);
    }

    @Override
    public void tick() {
        try {
            this.xo = this.x;
            this.yo = this.y;
            this.zo = this.z;
            if (!alive(this.age++)) {
                this.remove();
                return;
            }

            if (this.parametricMotion) {
                var para = this.parent.get(ParticleComponents.PARTICLE_MOTION_PARAMETRIC);
                this.x = this.parent.runtime().resolve(para.relativePosition.get(0));
                this.y = this.parent.runtime().resolve(para.relativePosition.get(1));
                this.z = this.parent.runtime().resolve(para.relativePosition.get(2));

                if (!para.direction.isEmpty()) {
                    this.xd = this.parent.runtime().resolve(para.direction.get(0));
                    this.yd = this.parent.runtime().resolve(para.direction.get(1));
                    this.zd = this.parent.runtime().resolve(para.direction.get(2));
                }

                this.roll = this.parent.runtime().resolve(para.rotation);
            }

            if (this.dynamicMotion) {
                var dynMotion = this.parent.get(ParticleComponents.PARTICLE_MOTION_DYNAMIC);
                var accel = dynMotion.linearAcceleration;
                var xa = this.parent.runtime().resolve(accel.get(0));
                var ya = this.parent.runtime().resolve(accel.get(1));
                var za = this.parent.runtime().resolve(accel.get(2));

                float dragCoefficient = this.parent.runtime().resolve(this.parent.get(ParticleComponents.PARTICLE_MOTION_DYNAMIC).linearDragCoefficient);
                xa -= dragCoefficient * this.xd;
                ya -= dragCoefficient * this.yd;
                za -= dragCoefficient * this.zd;

                this.xd += xa * Heatwave.TIME_SCALE;
                this.yd += ya * Heatwave.TIME_SCALE;
                this.zd += za * Heatwave.TIME_SCALE;

                this.move(
                        this.xd * Heatwave.TIME_SCALE,
                        this.yd * Heatwave.TIME_SCALE,
                        this.zd * Heatwave.TIME_SCALE
                );

                var ra = this.parent.runtime().resolve(dynMotion.rotationAcceleration);
                var rdc = this.parent.runtime().resolve(dynMotion.rotationDragCoefficient);
                ra -= rdc * this.rolld;
                this.rolld += ra * Heatwave.TIME_SCALE;

                if (this.rolld != 0)
                    this.roll += this.rolld * Heatwave.TIME_SCALE;
            }

            if (this.onGround) {
                this.xd *= 0.7f;
                this.zd *= 0.7f;
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

    public void setPos(double d, double d2, double d3) {
        this.x = d;
        this.y = d2;
        this.z = d3;
        float f = this.bbRadius;
        this.setBoundingBox(new AABB(d - (double)f, d2, d3 - (double)f, d + (double)f, d2 + (double)f, d3 + (double)f));
    }

    public void move(double nx, double ny, double nz) {
        if (this.parent.getAttachment() == null || this.parent.getAttachment().getWorld() == null)
            return;

        if (this.stoppedByCollision)
            return;

        double tmpX = nx;
        double tmpY = ny;
        double tmpZ = nz;

        if (this.physics) {
            if ((nx != 0.0 || ny != 0.0 || nz != 0.0) && nx * nx + ny * ny + nz * nz < MAX_COLLISION_VELOCITY) {
                Vec3 vec3 = Entity.collideBoundingBox(null, new Vec3(nx, ny, nz), this.getBoundingBox(), this.parent.getAttachment().getWorld(), List.of());
                nx = vec3.x;
                ny = vec3.y;
                nz = vec3.z;
                if (Math.abs(ny) < (double) 1.0E-5f) { // coll check
                    var coll = this.get(ParticleComponents.PARTICLE_MOTION_COLLISION);
                    if (coll.expireOnContact) {
                        this.remove();
                    }
                    else {
                        float bounciness = this.get(ParticleComponents.PARTICLE_MOTION_COLLISION).coefficientOfRestitution;
                        this.yd = bounciness * -this.yd;
                    }
                }
            }

        }

        if (nx != 0.0 || ny != 0.0 || nz != 0.0) {
            this.setBoundingBox(this.getBoundingBox().move(nx, ny, nz));
            this.setLocationFromBoundingbox();
        }
        if (Math.abs(tmpY) >= (double) 1.0E-5f && Math.abs(ny) < (double) 1.0E-5f && this.yd < (double) 1.0E-5f) {
            this.stoppedByCollision = true;
        }

        this.onGround = tmpY != ny && tmpY < 0.0;

        if (tmpX != nx) {
            this.xd = 0.f;
        }
        if (tmpZ != nz) {
            this.zd = 0.f;
        }
    }

    protected void setLocationFromBoundingbox() {
        AABB aABB = this.getBoundingBox();
        this.x = (aABB.minX + aABB.maxX) / 2.0;
        this.y = aABB.minY;
        this.z = (aABB.minZ + aABB.maxZ) / 2.0;
    }

    private void updateElementTick() throws MolangRuntimeException {
        this.setLeftRotation(new Quaternionf().rotateZ(this.roll * Mth.DEG_TO_RAD));

        if (this.parent.has(ParticleComponents.PARTICLE_APPEARANCE_BILLBOARD) && !this.parent.get(ParticleComponents.PARTICLE_APPEARANCE_BILLBOARD).size.isEmpty()) {
            var size = this.parent.get(ParticleComponents.PARTICLE_APPEARANCE_BILLBOARD).size;
            var x = this.parent.runtime().resolve(size.get(0));
            var y = this.parent.runtime().resolve(size.get(1));
            var scale = new Vector3f(Float.isNaN(x) ? 0 : x*3.f, Float.isNaN(y) ? 0 : y*3.f, 1);
            if (!this.getScale().equals(scale, 0.001f)) {
                this.setScale(scale);
                this.sendTrackerUpdates();
                this.setScale(scale);
            }
        }

        if (this.parent.has(ParticleComponents.PARTICLE_APPEARANCE_TINTING)) {
            var tinting = this.parent.get(ParticleComponents.PARTICLE_APPEARANCE_TINTING);
            if (tinting.isRGBA()) {
                this.item.set(DataComponents.DYED_COLOR, new DyedItemColor(tinting.rgba(this.parent.runtime()), false));
            }
            else {
                var color = this.parent.get(ParticleComponents.PARTICLE_APPEARANCE_TINTING).color.color(this.parent.runtime());
                this.item.set(DataComponents.DYED_COLOR, new DyedItemColor(color, false));
            }
            this.getDataTracker().set(DisplayTrackedData.Item.ITEM, this.item, false);
        } else if (this.getItem() != this.item) {
            this.item.set(DataComponents.DYED_COLOR, new DyedItemColor(0xFF_FF_FF, false));
            this.setItem(this.item);
        }

        this.startInterpolationIfDirty();
    }

    @Override
    protected void sendTrackerUpdates() {
        if (parent.getAttachment() != null && (parent.getAttachment().getWorld().getGameTime()%2==0 ||this.age <= 1))
            super.sendTrackerUpdates();
    }

    @Override
    protected void sendPositionUpdates() {
        if (parent.getAttachment() != null && (parent.getAttachment().getWorld().getGameTime()%2==0 || this.age <= 1))
            super.sendPositionUpdates();
    }

        public void remove() {
        this.removed = true;
    }

    public boolean isRemoved() {
        return this.removed;
    }

    public AABB getBoundingBox() {
        return this.bb;
    }

    public void setBoundingBox(AABB aABB) {
        this.bb = aABB;
    }
}
