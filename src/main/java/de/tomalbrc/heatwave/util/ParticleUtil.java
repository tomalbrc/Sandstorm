package de.tomalbrc.heatwave.util;

import de.tomalbrc.heatwave.Particles;
import de.tomalbrc.heatwave.io.ParticleEffectFile;
import de.tomalbrc.heatwave.polymer.ParticleEffectHolder;
import eu.pb4.polymer.virtualentity.api.attachment.ChunkAttachment;
import eu.pb4.polymer.virtualentity.api.attachment.EntityAttachment;
import gg.moonflower.molangcompiler.api.exception.MolangRuntimeException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector2f;

public class ParticleUtil {
    public static void emit(ResourceLocation resourceLocation, ServerLevel serverLevel, Vec3 position, Vector2f rotation) {
        for (ParticleEffectFile file : Particles.ALL) {
            if (resourceLocation.equals(file.effect.description.identifier)) {
                ParticleEffectHolder holder;
                try {
                    holder = new ParticleEffectHolder(file, serverLevel);
                } catch (MolangRuntimeException e) {
                    throw new RuntimeException(e);
                }
                ChunkAttachment.ofTicking(holder, serverLevel, position);
                return;
            }
        }
    }

    public static void emit(ResourceLocation resourceLocation, ServerLevel serverLevel, Entity entity) {
        for (ParticleEffectFile file : Particles.ALL) {
            if (resourceLocation.equals(file.effect.description.identifier)) {
                ParticleEffectHolder holder;
                try {
                    holder = new ParticleEffectHolder(file, serverLevel);
                    holder.setRotation(entity.getXRot(), entity.getYRot());
                } catch (MolangRuntimeException e) {
                    throw new RuntimeException(e);
                }
                EntityAttachment.ofTicking(holder, entity);
                return;
            }
        }
    }
}
