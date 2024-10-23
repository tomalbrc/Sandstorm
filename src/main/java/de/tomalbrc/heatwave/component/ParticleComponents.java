package de.tomalbrc.heatwave.component;

import de.tomalbrc.heatwave.Heatwave;
import de.tomalbrc.heatwave.component.builtin.EmitterInitialization;
import net.minecraft.resources.ResourceLocation;

@SuppressWarnings("unused")
public class ParticleComponents {
    public static final ParticleComponentType<EmitterInitialization, EmitterInitialization.Config> emitter_initialization = registerVanillaComponent("emitter_initialization", EmitterInitialization.class);


    private static <T extends ParticleComponent<E>,E> ParticleComponentType<T, E> registerVanillaComponent(String name, Class<T> type) {
        return ParticleComponentRegistry.registerComponent(ResourceLocation.fromNamespaceAndPath(Heatwave.MOD_ID, name), type);
    }

    private static <T extends ParticleComponent<E>,E> ParticleComponentType<T, E> registerComponent(String name, Class<T> type) {
        return ParticleComponentRegistry.registerComponent(ResourceLocation.fromNamespaceAndPath(Heatwave.MOD_ID, name), type);
    }

    public static void init() {
    }
}
