package de.tomalbrc.heatwave.polymer;

import eu.pb4.polymer.virtualentity.api.elements.ItemDisplayElement;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import java.util.List;

public class ParticleElement extends ItemDisplayElement {
    private static final AABB INITIAL_AABB = new AABB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
    private static final double MAX_COLLISION_VELOCITY = Mth.square(100.0);

    private final Matrix4f transform = new Matrix4f();
    private AABB bb = INITIAL_AABB;

    protected boolean onGround;
    protected boolean hasPhysics = true;
    private boolean stoppedByCollision;
    protected boolean removed;
    protected int age;
    protected int lifetime;
    protected float gravity;
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

    private final ParticleEffectHolder parent;

    public ParticleElement(ParticleEffectHolder particleEffectHolder) {
        this.parent = particleEffectHolder;

        this.setBillboardMode(Display.BillboardConstraints.CENTER);
        this.setSendPositionUpdates(false);
        this.setInterpolationDuration(2);
    }

    public int getAge() {
        return this.age;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }
        this.yd -= 0.04 * (double)this.gravity;
        this.move(this.xd, this.yd, this.zd);
        if (this.speedUpWhenYMotionIsBlocked && this.y == this.yo) {
            this.xd *= 1.1;
            this.zd *= 1.1;
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

    public void move(double nx, double ny, double nz) {
        if (this.parent.getAttachment() == null || this.parent.getAttachment().getWorld() == null)
            return;

        if (this.stoppedByCollision) {
            return;
        }
        double tmpX = nx;
        double tmpY = ny;
        double tmpZ = nz;

        if (this.hasPhysics && (nx != 0.0 || ny != 0.0 || nz != 0.0) && nx * nx + ny * ny + nz * nz < MAX_COLLISION_VELOCITY) {
            Vec3 vec3 = Entity.collideBoundingBox(null, new Vec3(nx, ny, nz), this.getBoundingBox(), this.parent.getAttachment().getWorld(), List.of());
            nx = vec3.x;
            ny = vec3.y;
            nz = vec3.z;
        }
        if (nx != 0.0 || ny != 0.0 || nz != 0.0) {
            this.setBoundingBox(this.getBoundingBox().move(nx, ny, nz));
            this.setLocationFromBoundingbox();
        }
        if (Math.abs(tmpY) >= (double)1.0E-5f && Math.abs(ny) < (double)1.0E-5f) {
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

    private void onTick() {
        this.setTransform(this.transform);
        this.startInterpolationIfDirty();
    }

    public void remove() {
        this.removed = true;
    }

    public Matrix4f getTransform() {
        return transform;
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
