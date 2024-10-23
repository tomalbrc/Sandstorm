package de.tomalbrc.heatwave.component.builtin;

import de.tomalbrc.heatwave.component.ParticleComponent;
import gg.moonflower.molangcompiler.api.MolangExpression;

import java.util.List;

public record EmitterShapePoint(Config config) implements ParticleComponent<EmitterShapePoint.Config> {
    public static class Config {
        List<MolangExpression> offset;
    }
}