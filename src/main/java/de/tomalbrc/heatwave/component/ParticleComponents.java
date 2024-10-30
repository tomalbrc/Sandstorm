package de.tomalbrc.heatwave.component;

import de.tomalbrc.heatwave.Heatwave;
import de.tomalbrc.heatwave.component.emitter.*;
import de.tomalbrc.heatwave.component.particle.*;
import net.minecraft.resources.ResourceLocation;

@SuppressWarnings("unused")
public class ParticleComponents {
    // Emitter Components
    public static final ParticleComponentType<EmitterInitialization> EMITTER_INITIALIZATION = registerVanillaComponent("emitter_initialization", EmitterInitialization.class);
    public static final ParticleComponentType<EmitterLifetimeLooping> EMITTER_LIFETIME_LOOPING = registerVanillaComponent("emitter_lifetime_looping", EmitterLifetimeLooping.class);
    public static final ParticleComponentType<EmitterLifetimeEvents> EMITTER_LIFETIME_EVENTS = registerVanillaComponent("emitter_lifetime_events", EmitterLifetimeEvents.class);
    public static final ParticleComponentType<EmitterLifetimeExpression> EMITTER_LIFETIME_EXPRESSION = registerVanillaComponent("emitter_lifetime_expression", EmitterLifetimeExpression.class);
    public static final ParticleComponentType<EmitterLifetimeOnce> EMITTER_LIFETIME_ONCE = registerVanillaComponent("emitter_lifetime_once", EmitterLifetimeOnce.class);
    public static final ParticleComponentType<EmitterShapeEntityAABB> EMITTER_SHAPE_ENTITY_AABB = registerVanillaComponent("emitter_shape_entity_aabb", EmitterShapeEntityAABB.class);
    public static final ParticleComponentType<EmitterShapeDisc> EMITTER_SHAPE_DISC = registerVanillaComponent("emitter_shape_disc", EmitterShapeDisc.class);
    public static final ParticleComponentType<EmitterShapeBox> EMITTER_SHAPE_BOX = registerVanillaComponent("emitter_shape_box", EmitterShapeBox.class);
    public static final ParticleComponentType<EmitterShapeCustom> EMITTER_SHAPE_CUSTOM = registerVanillaComponent("emitter_shape_custom", EmitterShapeCustom.class);
    public static final ParticleComponentType<EmitterShapePoint> EMITTER_SHAPE_POINT = registerVanillaComponent("emitter_shape_point", EmitterShapePoint.class);
    public static final ParticleComponentType<EmitterShapeSphere> EMITTER_SHAPE_SPHERE = registerVanillaComponent("emitter_shape_sphere", EmitterShapeSphere.class);
    public static final ParticleComponentType<EmitterRateInstant> EMITTER_RATE_INSTANT = registerVanillaComponent("emitter_rate_instant", EmitterRateInstant.class);
    public static final ParticleComponentType<EmitterRateManual> EMITTER_RATE_MANUAL = registerVanillaComponent("emitter_rate_manual", EmitterRateManual.class);
    public static final ParticleComponentType<EmitterRateSteady> EMITTER_RATE_STEADY = registerVanillaComponent("emitter_rate_steady", EmitterRateSteady.class);
    public static final ParticleComponentType<EmitterLocalSpace> EMITTER_LOCAL_SPACE = registerVanillaComponent("emitter_local_space", EmitterLocalSpace.class);

    // Particle Components
    public static final ParticleComponentType<ParticleLifetimeExpression> PARTICLE_LIFETIME_EXPRESSION = registerVanillaComponent("particle_lifetime_expression", ParticleLifetimeExpression.class);
    public static final ParticleComponentType<ParticleInitialSpeed> PARTICLE_INITIAL_SPEED = registerVanillaComponent("particle_initial_speed", ParticleInitialSpeed.class);
    public static final ParticleComponentType<ParticleInitialSpin> PARTICLE_INITIAL_SPIN = registerVanillaComponent("particle_initial_spin", ParticleInitialSpin.class);
    public static final ParticleComponentType<ParticleExpireIfInBlocks> PARTICLE_EXPIRE_IF_IN_BLOCKS = registerVanillaComponent("particle_expire_if_in_blocks", ParticleExpireIfInBlocks.class);
    public static final ParticleComponentType<ParticleExpireIfNotInBlocks> PARTICLE_EXPIRE_IF_NOT_IN_BLOCKS = registerVanillaComponent("particle_expire_if_not_in_blocks", ParticleExpireIfNotInBlocks.class);
    public static final ParticleComponentType<ParticleLifetimeEvents> PARTICLE_LIFETIME_EVENTS = registerVanillaComponent("particle_lifetime_events", ParticleLifetimeEvents.class);
    public static final ParticleComponentType<ParticleLifetimeKillPlane> PARTICLE_LIFETIME_KILL_PLANE = registerVanillaComponent("particle_lifetime_kill_plane", ParticleLifetimeKillPlane.class);
    public static final ParticleComponentType<ParticleMotionCollision> PARTICLE_MOTION_COLLISION = registerVanillaComponent("particle_motion_collision", ParticleMotionCollision.class);
    public static final ParticleComponentType<ParticleMotionDynamic> PARTICLE_MOTION_DYNAMIC = registerVanillaComponent("particle_motion_dynamic", ParticleMotionDynamic.class);
    public static final ParticleComponentType<ParticleMotionParametric> PARTICLE_MOTION_PARAMETRIC = registerVanillaComponent("particle_motion_parametric", ParticleMotionParametric.class);
    public static final ParticleComponentType<ParticleAppearanceBillboard> PARTICLE_APPEARANCE_BILLBOARD = registerVanillaComponent("particle_appearance_billboard", ParticleAppearanceBillboard.class);
    public static final ParticleComponentType<ParticleAppearanceLighting> PARTICLE_APPEARANCE_LIGHTING = registerVanillaComponent("particle_appearance_lighting", ParticleAppearanceLighting.class);
    public static final ParticleComponentType<ParticleAppearanceTinting> PARTICLE_APPEARANCE_TINTING = registerVanillaComponent("particle_appearance_tinting", ParticleAppearanceTinting.class);

    private static <T extends ParticleComponent<?>, E> ParticleComponentType<T> registerVanillaComponent(String name, Class<T> type) {
        return ParticleComponentRegistry.registerComponent(ResourceLocation.withDefaultNamespace(name), type);
    }

    private static <T extends ParticleComponent<?>, E> ParticleComponentType<T> registerComponent(String name, Class<T> type) {
        return ParticleComponentRegistry.registerComponent(ResourceLocation.fromNamespaceAndPath(Heatwave.MOD_ID, name), type);
    }

    public static void init() {
    }
}
