package de.tomalbrc.sandstorm.component.emitter;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.sandstorm.component.ParticleComponent;

public class EmitterLocalSpace implements ParticleComponent<EmitterLocalSpace> {
    @SerializedName("position")
    public boolean position = false; // default: false

    @SerializedName("rotation")
    public boolean rotation = false; // default: false

    @SerializedName("velocity")
    public boolean velocity = false; // default: false
}
