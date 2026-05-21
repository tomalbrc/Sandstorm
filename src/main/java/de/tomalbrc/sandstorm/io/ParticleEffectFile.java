package de.tomalbrc.sandstorm.io;

import com.google.gson.annotations.SerializedName;

import java.util.UUID;

public class ParticleEffectFile {
    @SerializedName("format_version")
    public String formatVersion;

    @SerializedName("particle_effect")
    public ParticleEffect effect;
}
