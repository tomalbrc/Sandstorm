package de.tomalbrc.heatwave.component.particle.config;

import com.google.gson.annotations.SerializedName;
import gg.moonflower.molangcompiler.api.MolangExpression;

import java.util.List;

public class DirectionConfig {
    @SerializedName("mode")
    public Mode mode = Mode.DERIVE_FROM_VELOCITY;

    @SerializedName("min_speed_threshold")
    public float minSpeedThreshold = 0.01f;

    @SerializedName("custom_direction")
    public List<MolangExpression> customDirection;

    public enum Mode {
        @SerializedName("derive_from_velocity")
        DERIVE_FROM_VELOCITY,
        @SerializedName("custom_direction")
        CUSTOM_DIRECTION
    }
}
