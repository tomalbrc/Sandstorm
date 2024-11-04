package de.tomalbrc.heatwave.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import de.tomalbrc.heatwave.Heatwave;
import de.tomalbrc.heatwave.Particles;
import de.tomalbrc.heatwave.io.ParticleEffectFile;
import de.tomalbrc.heatwave.polymer.ParticleEffectHolder;
import de.tomalbrc.heatwave.util.ParticleUtil;
import eu.pb4.polymer.virtualentity.api.attachment.ChunkAttachment;
import gg.moonflower.molangcompiler.api.exception.MolangRuntimeException;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class HeatwaveCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        var heatwaveNode = Commands
                .literal("heatwave").requires(Permissions.require("heatwave.command", 1)).executes(x -> {
                    x.getSource().sendSystemMessage(Component.literal("Heatwave 1.0"));
                    return Command.SINGLE_SUCCESS;
                });

        heatwaveNode.then(argument("effect", ResourceLocationArgument.id()).suggests(new ParticleSuggestionProvider()).executes(HeatwaveCommand::execute));
        heatwaveNode.then(argument("effect", ResourceLocationArgument.id()).suggests(new ParticleSuggestionProvider()).then(argument("position", Vec3Argument.vec3()).executes(HeatwaveCommand::executeAt)));
        heatwaveNode.then(literal("clear").executes(HeatwaveCommand::clear));

        dispatcher.register(heatwaveNode);
    }

    private static int clear(CommandContext<CommandSourceStack> commandSourceStackCommandContext) {
        List<ParticleEffectHolder> toRemove = new ObjectArrayList<>();
        toRemove.addAll(Heatwave.HOLDER);
        for (ParticleEffectHolder holder : toRemove) {
            holder.destroy();
        }
        Heatwave.HOLDER.clear();

        return Command.SINGLE_SUCCESS;
    }

    private static int execute(CommandContext<CommandSourceStack> context) {
        ResourceLocation effectString = ResourceLocationArgument.getId(context, "effect");
        ParticleUtil.emit(effectString, context.getSource().getLevel(), context.getSource().getPosition());
        return Command.SINGLE_SUCCESS;
    }

    private static int executeAt(CommandContext<CommandSourceStack> context) {
        ResourceLocation effectString = ResourceLocationArgument.getId(context, "effect");
        ParticleUtil.emit(effectString, context.getSource().getLevel(), Vec3Argument.getVec3(context,"position"));
        return Command.SINGLE_SUCCESS;
    }
}
