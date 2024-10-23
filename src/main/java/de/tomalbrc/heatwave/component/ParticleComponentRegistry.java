package de.tomalbrc.heatwave.component;

import de.tomalbrc.heatwave.Heatwave;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.resources.ResourceLocation;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

public class ParticleComponentRegistry {
    private static final Map<ResourceLocation, ParticleComponentType<? extends ParticleComponent<?>, ?>> COMPONENT_MAP = new Object2ObjectOpenHashMap<>();

    public static <T extends ParticleComponent<E>, E> ParticleComponentType<T, E> registerComponent(ResourceLocation resourceLocation, Class<T> type) {
        Class<E> configType = ParticleComponentRegistry.inferConfigType(type);
        ParticleComponentType<T, E> particleComponentType = new ParticleComponentType<>(resourceLocation, type, configType);
        COMPONENT_MAP.put(resourceLocation, particleComponentType);
        return particleComponentType;
    }

    @SuppressWarnings("unchecked")
    private static <E> Class<E> inferConfigType(Class<? extends ParticleComponent<?>> componentClass) {
        Type[] genericInterfaces = componentClass.getGenericInterfaces();
        for (Type genericInterface : genericInterfaces) {
            if (genericInterface instanceof ParameterizedType parameterizedType) {
                return (Class<E>) parameterizedType.getActualTypeArguments()[0];
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T extends ParticleComponent<E>, E> ParticleComponentType<T, E> getType(ResourceLocation key) {
        ParticleComponentType<?, ?> info = COMPONENT_MAP.get(key);
        if (info == null) {
            Heatwave.LOGGER.error("Could not find particle component {}", key);
            return null;
        }
        return (ParticleComponentType<T, E>) COMPONENT_MAP.get(info.id());
    }
}
