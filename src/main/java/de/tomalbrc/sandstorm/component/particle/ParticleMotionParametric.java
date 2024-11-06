package de.tomalbrc.sandstorm.component.particle;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.sandstorm.component.ParticleComponent;
import gg.moonflower.molangcompiler.api.MolangExpression;

public class ParticleMotionParametric implements ParticleComponent<ParticleMotionParametric> {
    @SerializedName("relative_position")
    public MolangExpression[] relativePosition = new MolangExpression[]{MolangExpression.ZERO, MolangExpression.ZERO, MolangExpression.ZERO};
    @SerializedName("direction")
    public MolangExpression[] direction = new MolangExpression[0];
    @SerializedName("rotation")
    public MolangExpression rotation = MolangExpression.ZERO;
}
