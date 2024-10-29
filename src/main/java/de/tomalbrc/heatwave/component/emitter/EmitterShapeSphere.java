package de.tomalbrc.heatwave.component.emitter;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.annotations.SerializedName;
import de.tomalbrc.heatwave.component.ParticleComponent;
import gg.moonflower.molangcompiler.api.MolangExpression;

import java.util.List;

public class EmitterShapeSphere implements ParticleComponent<EmitterShapeSphere> {
    @SerializedName("offset")
    public List<MolangExpression> offset = ImmutableList.of(MolangExpression.ZERO, MolangExpression.ZERO, MolangExpression.ZERO); // default: [0, 0, 0]

    @SerializedName("radius")
    public MolangExpression radius = MolangExpression.of(1); // default: 1

    @SerializedName("surface_only")
    public boolean surfaceOnly = false; // default: false

    @SerializedName("direction")
    public JsonElement direction = new JsonPrimitive("outwards"); // default: "outwards"
}
