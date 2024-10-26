package de.tomalbrc.heatwave.polymer;

import de.tomalbrc.heatwave.util.ParticleModels;
import de.tomalbrc.heatwave.util.ColorUtil;
import de.tomalbrc.heatwave.component.ParticleComponent;
import de.tomalbrc.heatwave.component.ParticleComponentType;
import de.tomalbrc.heatwave.component.ParticleComponents;
import eu.pb4.polymer.virtualentity.api.elements.ItemDisplayElement;
import eu.pb4.polymer.virtualentity.api.tracker.DisplayTrackedData;
import gg.moonflower.molangcompiler.api.MolangExpression;
import gg.moonflower.molangcompiler.api.MolangRuntime;
import gg.moonflower.molangcompiler.api.exception.MolangRuntimeException;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import java.util.List;

public class ParticleElement extends ItemDisplayElement {
    private static final AABB INITIAL_AABB = new AABB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
    private static final double MAX_COLLISION_VELOCITY = Mth.square(100.0);

    private final Matrix4f transform = new Matrix4f();
    private MolangExpression maxLifetime;
    private MolangExpression lifetimeExpression;
    private AABB bb = INITIAL_AABB;

    protected boolean onGround;
    private boolean stoppedByCollision;
    protected boolean removed;
    protected int age;
    protected float roll;
    protected float friction = 0.98f;
    protected boolean speedUpWhenYMotionIsBlocked = false;

    protected double xo;
    protected double yo;
    protected double zo;
    protected double x;
    protected double y;
    protected double z;
    protected double xd;
    protected double yd;
    protected double zd;

    protected float bbRadius = 0.6f;

    protected final float random_1 = (float) Math.random();
    protected final float random_2 = (float) Math.random();
    protected final float random_3 = (float) Math.random();
    protected final float random_4 = (float) Math.random();

    private final ParticleEffectHolder parent;

    public ParticleElement(ParticleEffectHolder particleEffectHolder) throws MolangRuntimeException {
        this.parent = particleEffectHolder;

        if (particleEffectHolder.has(ParticleComponents.PARTICLE_LIFETIME_EXPRESSION)) {
            this.maxLifetime = this.get(ParticleComponents.PARTICLE_LIFETIME_EXPRESSION).maxLifetime;
            this.lifetimeExpression = particleEffectHolder.get(ParticleComponents.PARTICLE_LIFETIME_EXPRESSION).expirationExpression;
        }

        this.setPos(particleEffectHolder.getPos().x, particleEffectHolder.getPos().y, particleEffectHolder.getPos().z);

        if (this.parent.has(ParticleComponents.PARTICLE_MOTION_COLLISION)) {
            this.bbRadius = this.parent.get(ParticleComponents.PARTICLE_MOTION_COLLISION).collisionRadius;
        }

        ItemStack item = ParticleModels.modelData(particleEffectHolder.getEffectFile()).asStack();
        item.set(DataComponents.DYED_COLOR, new DyedItemColor(ColorUtil.hslToRgb(this.maxLifetime == null ? 1 : (float) (1.f - this.age * (1.0 / 20.0) / parent.runtime().resolve(this.maxLifetime)),1,1), false));
        this.setItem(item);

        if (this.parent.has(ParticleComponents.PARTICLE_APPEARANCE_BILLBOARD))
            this.setBillboardMode(Display.BillboardConstraints.CENTER);

        this.setSendPositionUpdates(true);
        this.setInterpolationDuration(2);
        this.setTeleportDuration(1);
    }

    public void setDelta(double xd, double yd, double zd) {
        this.xd = xd + (Math.random() * 2.0 - 1.0) * (double)0.4f;
        this.yd = yd + (Math.random() * 2.0 - 1.0) * (double)0.4f;
        this.zd = zd + (Math.random() * 2.0 - 1.0) * (double)0.4f;
        double a = (Math.random() + Math.random() + 1.0) * (double)0.15f;
        double b = Math.sqrt(this.xd * this.xd + this.yd * this.yd + this.zd * this.zd);
        this.xd = this.xd / b * a * (double)0.4f;
        this.yd = this.yd / b * a * (double)0.4f + (double)0.1f;
        this.zd = this.zd / b * a * (double)0.4f;
    }

    @Override
    public Vec3 getCurrentPos() {
        return new Vec3(x,y+0.05f,z);
    }

    private <T extends ParticleComponent<?>> T get(ParticleComponentType<T> type) {
        return this.parent.get(type);
    }

    private boolean alive(int age) throws MolangRuntimeException {
        return this.maxLifetime != null && this.age*(1.0/20.0) < this.parent.runtime().resolve(this.maxLifetime) || this.lifetimeExpression != null && this.parent.runtime().resolve(this.lifetimeExpression) != 0;
    }

    private void modifyRuntime(MolangRuntime runtime) {
        var rt = runtime.edit();
        rt.setVariable("particle_random_1", this.random_1);
        rt.setVariable("particle_random_2", this.random_2);
        rt.setVariable("particle_random_3", this.random_3);
        rt.setVariable("particle_random_4", this.random_4);
    }

    @Override
    public void tick() {
        this.modifyRuntime(this.parent.runtime());

        try {
            this.xo = this.x;
            this.yo = this.y;
            this.zo = this.z;
            if (!alive(this.age++)) {
                this.remove();
                return;
            }

            if (this.parent.has(ParticleComponents.PARTICLE_MOTION_DYNAMIC)) {
                this.xd += this.parent.runtime().resolve(this.parent.get(ParticleComponents.PARTICLE_MOTION_DYNAMIC).linearAcceleration.get(0))*(1/16.0);
                this.yd += this.parent.runtime().resolve(this.parent.get(ParticleComponents.PARTICLE_MOTION_DYNAMIC).linearAcceleration.get(1))*(1/16.0);
                this.zd += this.parent.runtime().resolve(this.parent.get(ParticleComponents.PARTICLE_MOTION_DYNAMIC).linearAcceleration.get(2))*(1/16.0);
            }

            this.move(this.xd, this.yd, this.zd);

            if (this.parent.has(ParticleComponents.PARTICLE_MOTION_DYNAMIC) && (this.y == this.yo || this.x == this.xo || this.z == this.zo)) {
                var val = this.parent.runtime().resolve(this.parent.get(ParticleComponents.PARTICLE_MOTION_DYNAMIC).linearDragCoefficient);
                this.xd *= val;
                this.yd *= val;
                this.zd *= val;
            }

            this.xd *= this.friction;
            this.yd *= this.friction;
            this.zd *= this.friction;

            if (this.onGround) {
                this.xd *= 0.7f;
                this.zd *= 0.7f;
            }

            this.onTick();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
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

        if (parent.has(ParticleComponents.PARTICLE_MOTION_COLLISION) && (nx != 0.0 || ny != 0.0 || nz != 0.0) && nx * nx + ny * ny + nz * nz < MAX_COLLISION_VELOCITY) {
            Vec3 vec3 = Entity.collideBoundingBox(null, new Vec3(nx, ny, nz), this.getBoundingBox(), this.parent.getAttachment().getWorld(), List.of());
            nx = vec3.x;
            ny = vec3.y;
            nz = vec3.z;
            if (Math.abs(ny) < (double)1.0E-5f) {
                float coefficientOfRestitution = parent.get(ParticleComponents.PARTICLE_MOTION_COLLISION).coefficientOfRestitution;
                yd = coefficientOfRestitution * -this.yd;
            }
        }
        if (nx != 0.0 || ny != 0.0 || nz != 0.0) {
            this.setBoundingBox(this.getBoundingBox().move(nx, ny, nz));
            this.setLocationFromBoundingbox();
        }
        if (Math.abs(tmpY) >= (double)1.0E-5f && Math.abs(ny) < (double)1.0E-5f && this.yd < (double)1.0E-5f) {
            this.stoppedByCollision = true;
        }
        this.onGround = tmpY != ny && tmpY < 0.0;
        if (tmpX != nx) {
            this.xd = 0.0;
        }
        if (tmpZ != nz) {
            this.zd = 0.0;
        }
    }

    protected void setLocationFromBoundingbox() {
        AABB aABB = this.getBoundingBox();
        this.x = (aABB.minX + aABB.maxX) / 2.0;
        this.y = aABB.minY;
        this.z = (aABB.minZ + aABB.maxZ) / 2.0;
    }

    private void onTick() throws MolangRuntimeException {
        this.setTransform(this.transform);
        var item = this.getItem();
        var hue = this.maxLifetime == null ? 1 : (float) (1.f - (this.age * (1.0 / 20.0) / parent.runtime().resolve(this.maxLifetime)));
        item.set(DataComponents.DYED_COLOR, new DyedItemColor(ColorUtil.hslToRgb(hue,0.99f, 0.5f), false));
        this.getDataTracker().set(DisplayTrackedData.Item.ITEM, item, true);

        this.startInterpolationIfDirty();
        this.sendPositionUpdates();
        this.sendTrackerUpdates();
    }

    public void remove() {
        this.removed = true;
    }

    public Matrix4f getTransform() {
        return this.transform;
    }

    public void setTransform(Matrix4f transform) {
        this.transform.set(transform);
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
