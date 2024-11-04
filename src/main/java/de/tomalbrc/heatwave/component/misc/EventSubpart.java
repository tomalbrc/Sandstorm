package de.tomalbrc.heatwave.component.misc;

import com.google.common.collect.ImmutableList;
import com.google.gson.annotations.SerializedName;
import gg.moonflower.molangcompiler.api.MolangExpression;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Random;

public class EventSubpart {
    float weight = 1;
    public String command; // java extension
    @SerializedName("particle_effect")
    public ParticleEffect particleEffect;
    @SerializedName("sound_effect")
    public SoundEffect soundEffect;
    public MolangExpression expression;
    public String log;

    List<EventSubpart> sequence = ImmutableList.of();
    List<EventSubpart> randomize = ImmutableList.of();

    public List<EventSubpart> collect() {
        List<EventSubpart> list = new ObjectArrayList<>();
        list.add(this);

        for (EventSubpart subpart : this.sequence) {
            list.addAll(subpart.collect());
        }

        if (!this.randomize.isEmpty()) {
            list.add(this.getRandom());
        }

        return list;
    }

    private EventSubpart getRandom() {
        float totalWeight = 0;
        for (EventSubpart item : randomize) {
            totalWeight += item.weight;
        }

        float randomWeight = new Random().nextFloat(totalWeight);
        for (EventSubpart item : randomize) {
            randomWeight -= item.weight;
            if (randomWeight < 0) {
                return item;
            }
        }
        return null;
    }

    public record ParticleEffect(ResourceLocation effect,
                          Type type,
                          @SerializedName("pre_effect_expression")
                          MolangExpression preEffectExpression) {
        public enum Type {
            @SerializedName("emitter")
            EMITTER,
            @SerializedName("emitter_bound")
            EMITTER_BOUND,
            @SerializedName("particle")
            PARTICLE,
            @SerializedName("particle_with_velocity")
            PARTICLE_WITH_VELOCITY
        }
    }

    public record SoundEffect(@SerializedName("event_name") String eventName) {
    }
}
