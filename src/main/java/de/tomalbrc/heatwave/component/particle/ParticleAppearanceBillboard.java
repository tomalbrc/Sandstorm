package de.tomalbrc.heatwave.component.particle;

import com.google.common.collect.ImmutableList;
import com.google.gson.annotations.SerializedName;
import de.tomalbrc.heatwave.component.ParticleComponent;
import de.tomalbrc.heatwave.component.misc.DirectionConfig;
import gg.moonflower.molangcompiler.api.MolangExpression;

import java.util.List;

// Particle Appearance Components
public class ParticleAppearanceBillboard implements ParticleComponent<ParticleAppearanceBillboard> {
    @SerializedName("size")
    public List<MolangExpression> size = ImmutableList.of(MolangExpression.of(1.f), MolangExpression.of(1.f));

    @SerializedName("facing_camera_mode")
    public CameraMode cameraMode;

    @SerializedName("direction")
    public DirectionConfig direction = new DirectionConfig();

    public UvConfig uv;

    public static class UvConfig {
        @SerializedName("texture_width")
        public int textureWidth;
        @SerializedName("texture_height")
        public int textureHeight;

        public MolangExpression[] uv;

        @SerializedName("uv_size")
        public MolangExpression[] uvSize;

        public Flipbook flipbook;
    }

    public static class Flipbook {
        public MolangExpression[] base_UV;
        public float[] size_UV;
        public float[] step_UV;
        public float frames_per_second;
        public MolangExpression max_frame;
        public boolean stretch_to_lifetime = false;
        public boolean loop = false;
    }

    public enum CameraMode {
        @SerializedName("rotate_xyz")
        ROTATE_XYZ,
        @SerializedName("rotate_y")
        ROTATE_Y,
        @SerializedName("lookat_xyz")
        LOOKAT_XYZ,
        @SerializedName("lookat_y")
        LOOKAT_Y,
        @SerializedName("direction_x")
        DIRECTION_X,
        @SerializedName("direction_y")
        DIRECTION_Y,
        @SerializedName("direction_z")
        DIRECTION_Z,
        @SerializedName("emitter_transform_xy")
        EMITTER_TRANSFORM_XY,
        @SerializedName("emitter_transform_xz")
        EMITTER_TRANSFORM_XZ,
        @SerializedName("emitter_transform_yz")
        EMITTER_TRANSFORM_YZ
    }
}
