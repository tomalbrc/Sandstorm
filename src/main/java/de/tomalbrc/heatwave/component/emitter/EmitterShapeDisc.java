package de.tomalbrc.heatwave.component.emitter;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.annotations.SerializedName;
import de.tomalbrc.heatwave.component.ParticleComponent;
import gg.moonflower.molangcompiler.api.MolangExpression;

import java.util.List;

// Emitter Shape Components
public class EmitterShapeDisc implements ParticleComponent<EmitterShapeDisc> {
    @SerializedName("plane_normal")
    public List<MolangExpression> planeNormal = ImmutableList.of(MolangExpression.ZERO, MolangExpression.of(1), MolangExpression.ZERO); // default: [0, 1, 0]

    @SerializedName("offset")
    public List<MolangExpression> offset = ImmutableList.of(MolangExpression.ZERO, MolangExpression.ZERO, MolangExpression.ZERO); // default: [0, 0, 0]

    @SerializedName("radius")
    public MolangExpression radius = MolangExpression.of(1); // default: 1

    @SerializedName("surface_only")
    public boolean surfaceOnly = false; // default: false

    @SerializedName("direction")
    public JsonElement direction = new JsonPrimitive("outwards"); // default: "outwards"
}
