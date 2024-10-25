package de.tomalbrc.heatwave.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import de.tomalbrc.heatwave.polymer.ParticleEffectHolder;
import eu.pb4.polymer.virtualentity.api.attachment.ChunkAttachment;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class HeatwaveCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        var heatwaveNode = Commands
                .literal("heatwave").requires(Permissions.require("heatwave.command", 1));

        dispatcher.register(heatwaveNode.executes(HeatwaveCommand::execute));
    }

    private static int execute(CommandContext<CommandSourceStack> context) {
        var player = context.getSource().getPlayer();
        if (player != null) {
            var holder = new ParticleEffectHolder();
            ChunkAttachment.ofTicking(holder, player.serverLevel(), player.position());
        }
        return 0;
    }
}
