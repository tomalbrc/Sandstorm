package de.tomalbrc.sandstorm.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import de.tomalbrc.sandstorm.Sandstorm;
import de.tomalbrc.sandstorm.polymer.ParticleEffectHolder;
import de.tomalbrc.sandstorm.util.ParticleUtil;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector2f;

import java.util.List;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class SandstormCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        var sandstormNode = Commands.literal("sandstorm").requires(Permissions.require("sandstorm.command", 1)).executes(x -> {
            x.getSource().sendSystemMessage(Component.literal("Sandstorm 0.1-alpha by tomalbrc a.k.a. Pinnit"));
            return Command.SINGLE_SUCCESS;
        });

        sandstormNode.then(argument("effect", ResourceLocationArgument.id()).suggests(new ParticleSuggestionProvider()).executes(SandstormCommand::execute));
        sandstormNode.then(argument("effect", ResourceLocationArgument.id()).suggests(new ParticleSuggestionProvider()).then(argument("position", Vec3Argument.vec3()).executes(SandstormCommand::executeAt)));
        sandstormNode.then(literal("clear").executes(SandstormCommand::clear));

        dispatcher.register(sandstormNode);
    }

    private static int clear(CommandContext<CommandSourceStack> commandSourceStackCommandContext) {
        List<ParticleEffectHolder> toRemove = new ObjectArrayList<>();
        toRemove.addAll(Sandstorm.HOLDER);
        for (ParticleEffectHolder holder : toRemove) {
            holder.destroy();
        }
        Sandstorm.HOLDER.clear();

        return Command.SINGLE_SUCCESS;
    }

    private static int execute(CommandContext<CommandSourceStack> context) {
        ResourceLocation effectString = ResourceLocationArgument.getId(context, "effect");
        ParticleUtil.emit(effectString, context.getSource().getLevel(), context.getSource().getPosition(), new Vector2f(context.getSource().getRotation().x, context.getSource().getRotation().y));
        return Command.SINGLE_SUCCESS;
    }

    private static int executeAt(CommandContext<CommandSourceStack> context) {
        ResourceLocation effectString = ResourceLocationArgument.getId(context, "effect");
        ParticleUtil.emit(effectString, context.getSource().getLevel(), Vec3Argument.getVec3(context, "position"), new Vector2f(0, 0));
        return Command.SINGLE_SUCCESS;
    }
}
