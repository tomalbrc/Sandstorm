package de.tomalbrc.heatwave.component.misc;

import com.google.gson.annotations.SerializedName;

public class EventConfig {
    @SerializedName("event")
    public String event;
    @SerializedName("min_speed")
    public float minSpeed = 2.0f;
}
