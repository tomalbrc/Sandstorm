package de.tomalbrc.sandstorm.io;

import com.google.gson.annotations.SerializedName;

import java.util.UUID;

public class ParticleEffectFile {
    transient public UUID uuid = UUID.randomUUID();

    @SerializedName("format_version")
    public String formatVersion;

    @SerializedName("particle_effect")
    public ParticleEffect effect;
}
