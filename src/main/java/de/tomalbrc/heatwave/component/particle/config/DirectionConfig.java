package de.tomalbrc.heatwave.component.particle.config;

import com.google.gson.annotations.SerializedName;

public class DirectionConfig {
    @SerializedName("mode")
    public String mode = "derive_from_velocity";
    @SerializedName("min_speed_threshold")
    public float minSpeedThreshold = 0.01f;
    @SerializedName("custom_direction")
    public float[] customDirection = new float[3];
}
