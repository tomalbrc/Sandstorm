package de.tomalbrc.heatwave;

import com.mojang.logging.LogUtils;
import de.tomalbrc.heatwave.command.HeatwaveCommand;
import de.tomalbrc.heatwave.component.ParticleComponents;
import de.tomalbrc.heatwave.util.ParticleModels;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import gg.moonflower.molangcompiler.api.MolangCompiler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.slf4j.Logger;

public class Heatwave implements ModInitializer {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String MOD_ID = "heatwave";
    public static MolangCompiler MOLANG = MolangCompiler.create(MolangCompiler.DEFAULT_FLAGS, Heatwave.class.getClassLoader());

    @Override
    public void onInitialize() {
        ParticleComponents.init();
        Particles.init();

        CommandRegistrationCallback.EVENT.register((dispatcher, context, selection) -> {
            HeatwaveCommand.register(dispatcher);
        });

        PolymerResourcePackUtils.RESOURCE_PACK_CREATION_EVENT.register(ParticleModels::addToResourcePack);
    }
}
