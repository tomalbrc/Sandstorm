package de.tomalbrc.heatwave.component.particle;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.heatwave.component.ParticleComponent;
import de.tomalbrc.heatwave.component.particle.config.EventConfig;

import java.util.List;

// Particle Motion Components
public class ParticleMotionCollision implements ParticleComponent {
    @SerializedName("enabled")
    public boolean enabled = true;
    @SerializedName("collision_drag")
    public float collisionDrag;
    @SerializedName("coefficient_of_restitution")
    public float coefficientOfRestitution;
    @SerializedName("collision_radius")
    public float collisionRadius;
    @SerializedName("expire_on_contact")
    public boolean expireOnContact;
    @SerializedName("events")
    public List<EventConfig> events;
}
