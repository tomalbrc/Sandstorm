package de.tomalbrc.sandstorm.component.emitter;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.sandstorm.component.ParticleComponent;
import gg.moonflower.molangcompiler.api.MolangExpression;

public class EmitterShapePoint implements ParticleComponent<EmitterShapePoint> {
    @SerializedName("offset")
    public MolangExpression[] offset = new MolangExpression[]{MolangExpression.ZERO, MolangExpression.ZERO, MolangExpression.ZERO}; // default: [0, 0, 0]

    @SerializedName("direction")
    public MolangExpression[] direction = new MolangExpression[]{MolangExpression.ZERO, MolangExpression.ZERO, MolangExpression.ZERO}; // specify direction
}
