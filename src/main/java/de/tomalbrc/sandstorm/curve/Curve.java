package de.tomalbrc.sandstorm.curve;

import gg.moonflower.molangcompiler.api.MolangEnvironment;
import gg.moonflower.molangcompiler.api.exception.MolangRuntimeException;

public interface Curve {
    float evaluate(MolangEnvironment environment) throws MolangRuntimeException;
}
