package de.tomalbrc.sandstorm;

import de.tomalbrc.sandstorm.io.Json;
import de.tomalbrc.sandstorm.io.ParticleEffectFile;
import de.tomalbrc.sandstorm.util.ParticleModels;
import gg.moonflower.molangcompiler.api.exception.MolangRuntimeException;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.List;

public class Particles {
    public static final List<ParticleEffectFile> ALL = new ObjectArrayList<>();

    static {
        try {
            loadEffect("/particle/rainbow.json");
            loadEffect("/particle/bounce.json");
            loadEffect("/particle/snow.json");
            loadEffect("/particle/loading.json");
            loadEffect("/particle/trail.json");
            loadEffect("/particle/smoke.json");
            loadEffect("/particle/magic.json");

            loadEffect("/particle/rift.json");
            loadEffect("/particle/confetti.json");
            loadEffect("/particle/flame.json");
            loadEffect("/particle/combocurve.json");
            loadEffect("/particle/rain.json");
            loadEffect("/particle/event.json");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // read builtin particle file
    private static void loadEffect(String path) throws IOException {
        InputStream stream = Sandstorm.class.getResourceAsStream(path);
        loadEffect(stream);
    }

    public static ParticleEffectFile loadEffect(InputStream effectFileContents, InputStream imageContents) {
        ParticleEffectFile effectFile = Json.GSON.fromJson(new InputStreamReader(effectFileContents), ParticleEffectFile.class);
        return loadEffect(effectFile, imageContents);
    }

    public static ParticleEffectFile loadEffect(ParticleEffectFile effectFile, InputStream imageContents) {
        try {
            ParticleModels.addFrom(effectFile, imageContents);
        } catch (MolangRuntimeException | IOException e) {
            throw new RuntimeException(e);
        }

        ALL.add(effectFile);
        return effectFile;
    }

    // load particle effect file using textures from config or builtin
    public static ParticleEffectFile loadEffect(InputStream inputStream) {
        ParticleEffectFile effectFile = Json.GSON.fromJson(new InputStreamReader(inputStream), ParticleEffectFile.class);
        var texturePath = effectFile.effect.description.renderParameters.get("texture");
        return loadEffect(effectFile, readLocalImage(texturePath));
    }

    // read texture from configs or builtin, in that order
    public static InputStream readLocalImage(String path) {
        InputStream resource = null;
        try {
            resource = Files.newInputStream(Sandstorm.CONFIG_DIR.resolve(path + ".png"));
        } catch (IOException ignored) {

        }

        try {
            if (resource == null || resource.available() < 0) {
                resource = Sandstorm.class.getResourceAsStream("/" + path + ".png");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        assert resource != null;

        return resource;
    }

    public static void init() {}
}
