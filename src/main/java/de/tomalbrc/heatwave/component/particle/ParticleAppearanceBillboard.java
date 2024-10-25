package de.tomalbrc.heatwave.component.particle;

import com.google.common.collect.ImmutableList;
import com.google.gson.annotations.SerializedName;
import de.tomalbrc.heatwave.component.ParticleComponent;
import de.tomalbrc.heatwave.component.particle.config.DirectionConfig;
import gg.moonflower.molangcompiler.api.MolangExpression;

import java.util.List;


// Particle Appearance Components
public class ParticleAppearanceBillboard implements ParticleComponent<ParticleAppearanceBillboard> {
    @SerializedName("size")
    public List<MolangExpression> size = ImmutableList.of(MolangExpression.of(1.f), MolangExpression.of(1.f));

    @SerializedName("direction")
    public DirectionConfig direction = new DirectionConfig();
}
