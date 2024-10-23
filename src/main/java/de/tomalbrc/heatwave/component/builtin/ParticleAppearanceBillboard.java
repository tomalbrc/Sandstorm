package de.tomalbrc.heatwave.component.builtin;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import de.tomalbrc.heatwave.component.ParticleComponent;
import gg.moonflower.molangcompiler.api.MolangExpression;

import java.util.List;

// appearance for billboard particles
public record ParticleAppearanceBillboard(Config config) implements ParticleComponent<ParticleAppearanceBillboard.Config> {
    public static class Config {
        List<MolangExpression> size;

        @SerializedName("facing_camera_mode")
        FacingCameraMode facingCameraMode;

        JsonObject uv;
    }

    enum FacingCameraMode {
        @SerializedName("rotate_xyz")
        ROTATE_XYZ
    }
}