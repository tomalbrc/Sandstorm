package de.tomalbrc.heatwave.polymer;

import eu.pb4.polymer.virtualentity.api.elements.ItemDisplayElement;
import net.minecraft.world.entity.Display;
import org.joml.Matrix4f;

public class ParticleElement extends ItemDisplayElement {
    Matrix4f transform = new Matrix4f();

    public ParticleElement() {
        this.setBillboardMode(Display.BillboardConstraints.CENTER);
        this.setSendPositionUpdates(false);
    }

    @Override
    public void tick() {
        super.tick();
    }
}
