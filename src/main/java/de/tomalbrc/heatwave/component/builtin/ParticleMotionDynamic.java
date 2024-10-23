package de.tomalbrc.heatwave.component.builtin;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.heatwave.component.ParticleComponent;

import java.util.List;

public record ParticleMotionDynamic(Config config) implements ParticleComponent<ParticleMotionDynamic.Config> {
    public static class Config {
        @SerializedName("linear_acceleration")
        List<Double> linearAcceleration;
    }
}