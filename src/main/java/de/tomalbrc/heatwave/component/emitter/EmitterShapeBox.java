package de.tomalbrc.heatwave.component.emitter;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.heatwave.component.ParticleComponent;

public class EmitterShapeBox implements ParticleComponent {
    @SerializedName("offset")
    public float[] offset = new float[]{0.0f, 0.0f, 0.0f}; // default: [0, 0, 0]

    @SerializedName("half_dimensions")
    public float[] halfDimensions; // specify dimensions

    @SerializedName("surface_only")
    public boolean surfaceOnly = false; // default: false

    @SerializedName("direction")
    public Object direction = "outwards"; // default: "outwards"
}
