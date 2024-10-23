package de.tomalbrc.heatwave.io;

import com.google.gson.JsonObject;
import de.tomalbrc.heatwave.component.ParticleComponentMap;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class ParticleEffect {
    Description description;
    ParticleComponentMap components;
    Map<String, JsonObject> curves;

    public static class Description {
        public ResourceLocation identifier;
    }
}
