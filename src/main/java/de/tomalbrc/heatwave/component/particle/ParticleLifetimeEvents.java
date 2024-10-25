package de.tomalbrc.heatwave.component.particle;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.heatwave.component.ParticleComponent;

import java.util.Map;

public class ParticleLifetimeEvents implements ParticleComponent<ParticleLifetimeEvents> {
    @SerializedName("creation_event")
    public String[] creationEvent;
    @SerializedName("expiration_event")
    public String[] expirationEvent;
    @SerializedName("timeline")
    public Map<String, String> timeline;
}
