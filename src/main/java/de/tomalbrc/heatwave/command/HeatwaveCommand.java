package de.tomalbrc.heatwave.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
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
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.coordinates.Coordinates;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.commands.SummonCommand;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class HeatwaveCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        var heatwaveNode = Commands
                .literal("heatwave").requires(Permissions.require("heatwave.command", 1)).executes(x -> {
                    x.getSource().sendSystemMessage(Component.literal("Heatwave 1.0"));
                    return 0;
                });

        heatwaveNode.then(argument("effect", ResourceLocationArgument.id()).suggests(new ParticleSuggestionProvider()).executes(HeatwaveCommand::execute));
        heatwaveNode.then(argument("effect", ResourceLocationArgument.id()).suggests(new ParticleSuggestionProvider()).then(argument("position", Vec3Argument.vec3()).executes(HeatwaveCommand::executeAt)));
        heatwaveNode.then(literal("clear").executes(HeatwaveCommand::clear));

        dispatcher.register(heatwaveNode.executes(HeatwaveCommand::execute));
    }

    private static int clear(CommandContext<CommandSourceStack> commandSourceStackCommandContext) {
        for (ParticleEffectHolder holder : Heatwave.HOLDER) {
            holder.destroy();
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int execute(CommandContext<CommandSourceStack> context) {
        ParticleEffectFile file = effectFile(context);

        if (file != null && context.getSource() != null) {
            ParticleEffectHolder holder;
            try {
                holder = new ParticleEffectHolder(file, context.getSource().getLevel());
            } catch (MolangRuntimeException e) {
                throw new RuntimeException(e);
            }
            ChunkAttachment.ofTicking(holder, context.getSource().getLevel(), context.getSource().getPosition());
        }

        return Command.SINGLE_SUCCESS;
    }

    private static ParticleEffectFile effectFile(CommandContext<CommandSourceStack> context) {
        ParticleEffectFile file = null;
        String effectString = ResourceLocationArgument.getId(context, "effect").toString();
        for (ParticleEffectFile effectFile : Particles.ALL) {
            if (effectFile.effect.description.identifier.toString().equals(effectString)) {
                file = effectFile;
                break;
            }
        }
        return file;
    }

    private static int executeAt(CommandContext<CommandSourceStack> context) {
        ParticleEffectFile file = effectFile(context);

        if (file != null && context.getSource() != null) {
            ParticleEffectHolder holder;
            try {
                holder = new ParticleEffectHolder(file, context.getSource().getLevel());
            } catch (MolangRuntimeException e) {
                throw new RuntimeException(e);
            }
            ChunkAttachment.ofTicking(holder, context.getSource().getLevel(), Vec3Argument.getVec3(context,""));
        }

        return Command.SINGLE_SUCCESS;
    }
}
