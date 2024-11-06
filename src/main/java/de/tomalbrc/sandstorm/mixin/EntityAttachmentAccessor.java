package de.tomalbrc.sandstorm.mixin;

import eu.pb4.polymer.virtualentity.api.attachment.EntityAttachment;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = EntityAttachment.class, remap = false)
public interface EntityAttachmentAccessor {
    @Accessor
    public Entity getEntity();
}
