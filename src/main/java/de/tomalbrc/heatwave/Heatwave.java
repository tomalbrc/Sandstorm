package de.tomalbrc.heatwave;

import com.mojang.logging.LogUtils;
import de.tomalbrc.heatwave.io.Json;
import de.tomalbrc.heatwave.io.ParticleEffectFile;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;

import java.io.InputStream;
import java.io.InputStreamReader;

public class Heatwave implements ModInitializer {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String MOD_ID = "heatwave";

    @Override
    public void onInitialize() {
        InputStream stream = Heatwave.class.getResourceAsStream("/particle/rainbow.particle.json");
        if (stream != null) {
            ParticleEffectFile effectFile = Json.GSON.fromJson(new InputStreamReader(stream), ParticleEffectFile.class);
            LOGGER.info(effectFile.toString());
        }
    }
}
