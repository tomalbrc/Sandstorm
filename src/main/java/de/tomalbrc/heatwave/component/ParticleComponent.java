package de.tomalbrc.heatwave.component;

public interface ParticleComponent<P> {
    @SuppressWarnings("unchecked")
    default P value() {
        return (P) this;
    }
}
