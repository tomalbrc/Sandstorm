package de.tomalbrc.sandstorm.component.particle;

import com.google.gson.*;
import com.google.gson.annotations.SerializedName;
import de.tomalbrc.sandstorm.component.ParticleComponent;
import de.tomalbrc.sandstorm.component.misc.ColorConfig;
import gg.moonflower.molangcompiler.api.MolangExpression;
import gg.moonflower.molangcompiler.api.MolangRuntime;
import gg.moonflower.molangcompiler.api.exception.MolangRuntimeException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ParticleAppearanceTinting implements ParticleComponent<ParticleAppearanceTinting> {
    @SerializedName("color")
    public ColorConfig color = new ColorConfig();

    @SerializedName("color")
    public List<MolangExpression> rgba;

    public boolean isRGBA() {
        return this.rgba != null;
    }

    public int rgba(MolangRuntime runtime) throws MolangRuntimeException {
        if (rgba == null || rgba.size() < 3) {
            throw new IllegalArgumentException("RGBA list must contain at least three components (R, G, B).");
        }

        int red = (int) (Math.max(0, Math.min(1, runtime.resolve(rgba.get(0)))) * 255);
        int green = (int) (Math.max(0, Math.min(1, runtime.resolve(rgba.get(1)))) * 255);
        int blue = (int) (Math.max(0, Math.min(1, runtime.resolve(rgba.get(2)))) * 255);
        int alpha = rgba.size() > 3 ? (int) (Math.max(0, Math.min(1, runtime.resolve(rgba.get(3)))) * 255) : 255;
        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }

    public static class Deserializer implements JsonDeserializer<ParticleAppearanceTinting> {
        @Override
        public ParticleAppearanceTinting deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            ParticleAppearanceTinting result = new ParticleAppearanceTinting();
            JsonObject jsonObject = json.getAsJsonObject();

            JsonElement colorElement = jsonObject.get("color");

            if (colorElement.isJsonObject()) {
                result.color = context.deserialize(colorElement, ColorConfig.class);
                result.rgba = null;
            } else if (colorElement.isJsonArray()) {
                result.color = null;
                List<MolangExpression> rgbaList = new ArrayList<>();
                for (JsonElement element : colorElement.getAsJsonArray()) {
                    rgbaList.add(context.deserialize(element, MolangExpression.class));
                }
                result.rgba = rgbaList;
            } else {
                throw new JsonParseException("Unexpected JSON type for 'color'");
            }
            return result;
        }
    }
}
