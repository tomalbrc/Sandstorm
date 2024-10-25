package de.tomalbrc.heatwave.component.emitter;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.annotations.SerializedName;
import de.tomalbrc.heatwave.component.ParticleComponent;
import gg.moonflower.molangcompiler.api.MolangExpression;

public class EmitterShapeSphere implements ParticleComponent<EmitterShapeSphere> {
    @SerializedName("offset")
    public float[] offset = new float[]{0.0f, 0.0f, 0.0f}; // default: [0, 0, 0]

    @SerializedName("radius")
    public MolangExpression radius = MolangExpression.of(1); // default: 1

    @SerializedName("surface_only")
    public boolean surfaceOnly = false; // default: false

    @SerializedName("direction")
    public JsonElement direction = new JsonPrimitive("outwards"); // default: "outwards"
}
