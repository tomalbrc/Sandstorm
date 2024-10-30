package de.tomalbrc.heatwave;

import de.tomalbrc.heatwave.io.Json;
import de.tomalbrc.heatwave.io.ParticleEffectFile;
import de.tomalbrc.heatwave.util.ParticleModels;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class Particles {
    public static final List<ParticleEffectFile> ALL = new ObjectArrayList<>();

    public static final ParticleEffectFile RAINBOW;
    public static final ParticleEffectFile BALL;
    public static final ParticleEffectFile SNOW;
    public static final ParticleEffectFile LOADING;
    public static final ParticleEffectFile TRAIL;
    public static final ParticleEffectFile SMOKE;
    public static final ParticleEffectFile MAGIC;

    static {
        try {
            RAINBOW = loadEffect("/particle/rainbow.particle.json");
            BALL = loadEffect("/particle/ball.json");
            SNOW = loadEffect("/particle/snow.json");
            LOADING = loadEffect("/particle/loading.json");
            TRAIL = loadEffect("/particle/trail.json");
            SMOKE = loadEffect("/particle/smoke.json");
            MAGIC = loadEffect("/particle/magic.json");

            //loadEffect("/particle/rift.json");
            loadEffect("/particle/confetti.particle.json");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static ParticleEffectFile loadEffect(String path) throws IOException {
        InputStream stream = Heatwave.class.getResourceAsStream(path);
        if (stream != null) {
            ParticleEffectFile effectFile = Json.GSON.fromJson(new InputStreamReader(stream), ParticleEffectFile.class);
            ParticleModels.addFrom(effectFile);
            ALL.add(effectFile);
            return effectFile;
        }
        throw new RuntimeException(String.format("Could not load particle file %s", path));
    }

    public static void init() {}
}
