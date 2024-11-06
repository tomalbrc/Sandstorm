package de.tomalbrc.sandstorm.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import de.tomalbrc.sandstorm.Particles;
import de.tomalbrc.sandstorm.io.ParticleEffectFile;
import net.minecraft.commands.CommandSourceStack;

import java.util.concurrent.CompletableFuture;

class ParticleSuggestionProvider implements SuggestionProvider<CommandSourceStack> {
    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSourceStack> context,
                                                         SuggestionsBuilder builder) {
        for (ParticleEffectFile effectFile : Particles.ALL) {
            if (effectFile.effect != null && (builder.getRemaining().isBlank() || effectFile.effect.description.identifier.toString().contains(builder.getRemaining()))) {
                builder.suggest(effectFile.effect.description.identifier.toString());
            }
        }

        return builder.buildFuture();
    }
}