package de.tomalbrc.sandstorm.io;

import com.google.common.collect.ImmutableMap;
import com.google.gson.annotations.SerializedName;
import de.tomalbrc.sandstorm.component.ParticleComponentMap;
import de.tomalbrc.sandstorm.component.misc.EventSubpart;
import de.tomalbrc.sandstorm.curve.Curve;
import net.minecraft.resources.Identifier;

import java.util.Map;

public class ParticleEffect {
    public Description description;
    public ParticleComponentMap components;
    public Map<String, EventSubpart> events = ImmutableMap.of();

    public Map<String, Curve> curves = ImmutableMap.of();

    public static class Description {
        public Identifier identifier;

        @SerializedName("basic_render_parameters")
        public Map<String, String> renderParameters;
    }
}
