package de.tomalbrc.heatwave.component.emitter;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.heatwave.component.ParticleComponent;
import de.tomalbrc.heatwave.component.misc.Timeline;

public class EmitterLifetimeEvents implements ParticleComponent<EmitterLifetimeEvents> {
    @SerializedName("creation_event")
    public String[] creationEvent = new String[0]; // can be a list or single string
    @SerializedName("expiration_event")
    public String[] expirationEvent = new String[0]; // can be a list or single string

    @SerializedName("timeline")
    public Timeline timeline; // time to event mapping
}

