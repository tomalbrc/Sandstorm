package de.tomalbrc.sandstorm.component;

import de.tomalbrc.sandstorm.Sandstorm;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class ParticleComponentRegistry {
    private static final Map<ResourceLocation, ParticleComponentType<? extends ParticleComponent<?>>> COMPONENT_MAP = new Object2ObjectOpenHashMap<>();

    public static <T extends ParticleComponent<?>> ParticleComponentType<T> registerComponent(ResourceLocation resourceLocation, Class<T> type) {
        ParticleComponentType<T> particleComponentType = new ParticleComponentType<>(resourceLocation, type);
        COMPONENT_MAP.put(resourceLocation, particleComponentType);
        return particleComponentType;
    }

    @SuppressWarnings("unchecked")
    public static <T extends ParticleComponent<?>> ParticleComponentType<T> getType(ResourceLocation key) {
        ParticleComponentType<?> info = COMPONENT_MAP.get(key);
        if (info == null) {
            Sandstorm.LOGGER.error("Could not find particle component {}", key);
            return null;
        }
        return (ParticleComponentType<T>) COMPONENT_MAP.get(info.id());
    }
}
