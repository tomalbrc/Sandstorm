package de.tomalbrc.heatwave.component.particle;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.heatwave.component.ParticleComponent;
import net.minecraft.world.level.block.Block;

public class ParticleExpireIfNotInBlocks implements ParticleComponent<ParticleExpireIfNotInBlocks> {
    @SerializedName("blocks")
    public Block[] blocks;
}
