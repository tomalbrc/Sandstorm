package de.tomalbrc.heatwave.component;

public interface ParticleComponent<P> {
    default boolean isPrimitive() {
        return this.value() instanceof ParticleComponent<?>;
    }

    @SuppressWarnings("unchecked")
    default P value() {
        return (P) this;
    }
}
