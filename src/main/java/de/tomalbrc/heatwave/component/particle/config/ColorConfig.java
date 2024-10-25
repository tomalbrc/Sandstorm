package de.tomalbrc.heatwave.component.particle.config;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import gg.moonflower.molangcompiler.api.MolangExpression;

import java.util.List;
import java.util.Map;

public class ColorConfig {
    @SerializedName("gradient")
    public Map<String, JsonObject> gradient;
    @SerializedName("interpolant")
    public MolangExpression interpolant;
}
