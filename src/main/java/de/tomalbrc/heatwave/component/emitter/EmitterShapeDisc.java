package de.tomalbrc.heatwave.component.emitter;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.heatwave.component.ParticleComponent;
import gg.moonflower.molangcompiler.api.MolangExpression;

// Emitter Shape Components
public class EmitterShapeDisc implements ParticleComponent {
    @SerializedName("plane_normal")
    public Object planeNormal = new float[]{0.0f, 1.0f, 0.0f}; // default: [0, 1, 0]

    @SerializedName("offset")
    public float[] offset = new float[]{0.0f, 0.0f, 0.0f}; // default: [0, 0, 0]

    @SerializedName("radius")
    public MolangExpression radius = MolangExpression.of(1); // default: 1

    @SerializedName("surface_only")
    public boolean surfaceOnly = false; // default: false

    @SerializedName("direction")
    public Object direction = "outwards"; // default: "outwards"
}
