package de.tomalbrc.heatwave.io;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import de.tomalbrc.heatwave.component.ParticleComponentMap;
import de.tomalbrc.heatwave.curve.Curve;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class ParticleEffect {
    public Description description;
    public ParticleComponentMap components;

    public Map<String, Curve> curves;

    @SerializedName("basic_render_parameters")
    public Map<String, String> renderParameters;

    public static class Description {
        public ResourceLocation identifier;
    }
}
