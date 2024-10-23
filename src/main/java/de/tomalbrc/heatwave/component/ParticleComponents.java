package de.tomalbrc.heatwave.component;

import de.tomalbrc.heatwave.Heatwave;
import de.tomalbrc.heatwave.component.builtin.*;
import net.minecraft.resources.ResourceLocation;

@SuppressWarnings("unused")
public class ParticleComponents {
    public static final ParticleComponentType<EmitterInitialization, EmitterInitialization.Config> EMITTER_INITIALIZATION = registerVanillaComponent("emitter_initialization", EmitterInitialization.class);
    public static final ParticleComponentType<EmitterRateSteady, EmitterRateSteady.Config> EMITTER_RATE_STEADY = registerVanillaComponent("emitter_rate_steady", EmitterRateSteady.class);
    public static final ParticleComponentType<EmitterLifetimeLooping, EmitterLifetimeLooping.Config> EMITTER_LIFETIME_LOOPING = registerVanillaComponent("emitter_lifetime_looping", EmitterLifetimeLooping.class);
    public static final ParticleComponentType<EmitterShapePoint, EmitterShapePoint.Config> EMITTER_SHAPE_POINT = registerVanillaComponent("emitter_shape_point", EmitterShapePoint.class);
    public static final ParticleComponentType<ParticleLifetimeExpression, ParticleLifetimeExpression.Config> PARTICLE_LIFETIME_EXPRESSION = registerVanillaComponent("particle_lifetime_expression", ParticleLifetimeExpression.class);
    public static final ParticleComponentType<ParticleInitialSpeed, Double> PARTICLE_INITIAL_SPEED = registerVanillaComponent("particle_initial_speed", ParticleInitialSpeed.class);
    public static final ParticleComponentType<ParticleMotionDynamic, ParticleMotionDynamic.Config> PARTICLE_MOTION_DYNAMIC = registerVanillaComponent("particle_motion_dynamic", ParticleMotionDynamic.class);
    public static final ParticleComponentType<ParticleAppearanceBillboard, ParticleAppearanceBillboard.Config> PARTICLE_APPEARANCE_BILLBOARD = registerVanillaComponent("particle_appearance_billboard", ParticleAppearanceBillboard.class);
    public static final ParticleComponentType<ParticleAppearanceTinting, ParticleAppearanceTinting.Config> PARTICLE_APPEARANCE_TINTING = registerVanillaComponent("particle_appearance_tinting", ParticleAppearanceTinting.class);

    private static <T extends ParticleComponent<E>, E> ParticleComponentType<T, E> registerVanillaComponent(String name, Class<T> type) {
        return ParticleComponentRegistry.registerComponent(ResourceLocation.withDefaultNamespace(name), type);
    }

    private static <T extends ParticleComponent<E>, E> ParticleComponentType<T, E> registerComponent(String name, Class<T> type) {
        return ParticleComponentRegistry.registerComponent(ResourceLocation.fromNamespaceAndPath(Heatwave.MOD_ID, name), type);
    }

    public static void init() {
    }
}
