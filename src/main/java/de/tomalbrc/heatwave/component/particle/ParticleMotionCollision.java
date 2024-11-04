package de.tomalbrc.heatwave.component.particle;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.heatwave.component.ParticleComponent;
import de.tomalbrc.heatwave.component.misc.ConditionedEvent;
import gg.moonflower.molangcompiler.api.MolangExpression;

import java.util.List;

// Particle Motion Components
public class ParticleMotionCollision implements ParticleComponent<ParticleMotionCollision> {
    @SerializedName("enabled")
    public MolangExpression enabled = MolangExpression.of(true);
    @SerializedName("collision_drag")
    public float collisionDrag;
    @SerializedName("coefficient_of_restitution")
    public float coefficientOfRestitution;
    @SerializedName("collision_radius")
    public float collisionRadius;
    @SerializedName("expire_on_contact")
    public boolean expireOnContact;
    @SerializedName("events")
    public ConditionedEvent[] events = new ConditionedEvent[0];
}
