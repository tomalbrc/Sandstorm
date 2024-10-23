package de.tomalbrc.heatwave.component;

import net.minecraft.resources.ResourceLocation;

import java.lang.reflect.InvocationTargetException;

public record ParticleComponentType<T extends ParticleComponent<C>, C>(ResourceLocation id, Class<T> type,
                                                                       Class<C> configType) {
    public T createInstance(C object) {
        try {
            return type.getDeclaredConstructor(this.configType()).newInstance(object);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
