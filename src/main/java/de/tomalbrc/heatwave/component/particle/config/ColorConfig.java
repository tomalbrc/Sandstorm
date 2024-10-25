package de.tomalbrc.heatwave.component.particle.config;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ColorConfig {
    @SerializedName("gradient")
    public List<String> gradient;
    @SerializedName("interpolant")
    public float interpolant;
}
