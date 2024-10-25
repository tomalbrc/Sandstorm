package de.tomalbrc.heatwave.component;

public interface ParticleComponentHolder {

    ParticleComponentMap components();

    default <T extends ParticleComponent<?>> T get(ParticleComponentType<T> componentType) {
        return this.components().get(componentType);
    }

    default <T extends ParticleComponent<?>> boolean has(ParticleComponentType<T> componentType) {
        return this.components().has(componentType);
    }

    default <T extends ParticleComponent<?>> void set(ParticleComponentType<T> behaviourType, T value) {
        this.components().set(behaviourType, value);
    }

    default void initComponents(ParticleComponentMap componentMap) {
        this.components().from(componentMap);
    }
}