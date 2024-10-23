package de.tomalbrc.heatwave.core;

import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.elements.ItemDisplayElement;
import gg.moonflower.molangcompiler.api.MolangEnvironment;
import gg.moonflower.molangcompiler.api.MolangRuntime;
import gg.moonflower.molangcompiler.api.exception.MolangRuntimeException;

import java.util.List;

public class Emitter {
    ElementHolder holder;
    List<ItemDisplayElement> particleElements;
    MolangRuntime.Builder runtimeBuilder;

    public Emitter(ElementHolder holder) {
        this.runtimeBuilder = MolangRuntime.runtime();
        this.holder = holder;
    }

    public void tick() {

    }
}
