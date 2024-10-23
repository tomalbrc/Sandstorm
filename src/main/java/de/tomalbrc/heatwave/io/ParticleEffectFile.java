package de.tomalbrc.heatwave.io;

import com.google.gson.annotations.SerializedName;

public class ParticleEffectFile {
    @SerializedName("format_version")
    String formatVersion;

    @SerializedName("particle_effect")
    ParticleEffect effect;
}
