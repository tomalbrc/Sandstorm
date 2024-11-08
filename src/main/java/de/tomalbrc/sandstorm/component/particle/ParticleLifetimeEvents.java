package de.tomalbrc.sandstorm.component.particle;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.sandstorm.component.ParticleComponent;
import de.tomalbrc.sandstorm.component.misc.Timeline;

public class ParticleLifetimeEvents implements ParticleComponent<ParticleLifetimeEvents> {
    @SerializedName("creation_event")
    public String[] creationEvent = new String[0];
    @SerializedName("expiration_event")
    public String[] expirationEvent = new String[0];
    @SerializedName("timeline")
    public Timeline timeline; // time to event mapping
}
