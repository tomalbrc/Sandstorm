package de.tomalbrc.sandstorm.component;

public interface ParticleComponent<P> {
    @SuppressWarnings("unchecked")
    default P value() {
        return (P) this;
    }
}
