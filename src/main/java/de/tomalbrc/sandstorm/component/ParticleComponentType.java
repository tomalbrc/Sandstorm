package de.tomalbrc.sandstorm.component;

import net.minecraft.resources.ResourceLocation;

public record ParticleComponentType<T extends ParticleComponent<?>>(ResourceLocation id, Class<T> type) {

}
