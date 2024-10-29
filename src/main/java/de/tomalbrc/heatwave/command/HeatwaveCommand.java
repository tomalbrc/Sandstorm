package de.tomalbrc.heatwave.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import de.tomalbrc.heatwave.Heatwave;
import de.tomalbrc.heatwave.Particles;
import de.tomalbrc.heatwave.io.ParticleEffectFile;
import de.tomalbrc.heatwave.polymer.ParticleEffectHolder;
import eu.pb4.polymer.virtualentity.api.attachment.ChunkAttachment;
import gg.moonflower.molangcompiler.api.exception.MolangRuntimeException;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceLocationArgument;

import static net.minecraft.commands.Commands.argument;

public class HeatwaveCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        var heatwaveNode = Commands
                .literal("heatwave").requires(Permissions.require("heatwave.command", 1));

        heatwaveNode.then(argument("effect", ResourceLocationArgument.id()).suggests(new ParticleSuggestionProvider()).executes(HeatwaveCommand::execute));

        dispatcher.register(heatwaveNode.executes(HeatwaveCommand::execute));
    }

    private static int execute(CommandContext<CommandSourceStack> context) {
        var player = context.getSource().getPlayer();
        if (player != null) {
            ParticleEffectFile file = null;
            String effectString = ResourceLocationArgument.getId(context, "effect").toString();
            for (ParticleEffectFile effectFile : Particles.ALL) {
                if (effectFile.effect.description.identifier.toString().equals(effectString)) {
                    file = effectFile;
                    break;
                }
            }

            if (file != null) {
                ParticleEffectHolder holder;
                try {
                    holder = new ParticleEffectHolder(file);
                } catch (MolangRuntimeException e) {
                    throw new RuntimeException(e);
                }
                ChunkAttachment.ofTicking(holder, player.serverLevel(), player.position());
            }
        }
        return 0;
    }
}
