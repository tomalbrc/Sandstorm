package de.tomalbrc.heatwave.component.particle;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.heatwave.component.ParticleComponent;
import net.minecraft.world.level.block.Block;

// Particle Lifetime Components
public class ParticleExpireIfInBlocks implements ParticleComponent<ParticleExpireIfInBlocks> {
    @SerializedName("blocks")
    public Block[] blocks;
}
