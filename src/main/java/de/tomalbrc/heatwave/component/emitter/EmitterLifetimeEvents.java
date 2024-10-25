package de.tomalbrc.heatwave.component.emitter;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.heatwave.component.ParticleComponent;

import java.util.List;
import java.util.Map;

public class EmitterLifetimeEvents implements ParticleComponent<EmitterLifetimeEvents> {
    @SerializedName("creation_event")
    public List<String> creationEvent; // can be a list or single string
    @SerializedName("expiration_event")
    public List<String> expirationEvent; // can be a list or single string

    @SerializedName("timeline")
    public Map<String, String> timeline; // time to event mapping
}

