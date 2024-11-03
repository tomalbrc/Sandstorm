package de.tomalbrc.heatwave.component.misc;

import com.google.gson.*;
import com.google.gson.annotations.SerializedName;
import de.tomalbrc.heatwave.util.ColorUtil;
import gg.moonflower.molangcompiler.api.MolangExpression;
import gg.moonflower.molangcompiler.api.MolangRuntime;
import gg.moonflower.molangcompiler.api.exception.MolangRuntimeException;
import net.minecraft.util.Mth;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class ColorConfig {
    @SerializedName("gradient")
    public List<GradientStop> gradient;
    @SerializedName("interpolant")
    public MolangExpression interpolant;

    public int color(MolangRuntime runtime) throws MolangRuntimeException {
        var interpolantValue = runtime.resolve(interpolant);
        return getInterpolatedColor(this.gradient, Mth.clamp(interpolantValue, 0.f,1.f));
    }

    public record GradientStop(float position, int color) {}

    private int getInterpolatedColor(List<GradientStop> gradientStops, float variableRainbow) {
        GradientStop lowerStop = null;
        GradientStop upperStop = null;
        for (int i = 0; i < gradientStops.size() - 1; i++) {
            lowerStop = gradientStops.get(i);
            upperStop = gradientStops.get(i + 1);
            if (lowerStop.position < 0 || variableRainbow >= lowerStop.position && variableRainbow <= upperStop.position) {
                break;
            }
        }
        assert lowerStop != null && upperStop != null;
        return interpolateColor(lowerStop.color, upperStop.color, variableRainbow);
    }

    private int interpolateColor(int lowerColor, int upperColor, float t) {
        int lowerAlpha = (lowerColor >> 24) & 0xFF;
        int lowerRed   = (lowerColor >> 16) & 0xFF;
        int lowerGreen = (lowerColor >> 8) & 0xFF;
        int lowerBlue  = lowerColor & 0xFF;

        int upperAlpha = (upperColor >> 24) & 0xFF;
        int upperRed   = (upperColor >> 16) & 0xFF;
        int upperGreen = (upperColor >> 8) & 0xFF;
        int upperBlue  = upperColor & 0xFF;

        int interpAlpha = (int) Mth.lerp(t, lowerAlpha, upperAlpha);
        int interpRed   = (int) Mth.lerp(t, lowerRed, upperRed);
        int interpGreen = (int) Mth.lerp(t, lowerGreen, upperGreen);
        int interpBlue  = (int) Mth.lerp(t, lowerBlue, upperBlue);

        return (interpAlpha << 24) | (interpRed << 16) | (interpGreen << 8) | interpBlue;
    }

    public static class Deserializer implements JsonDeserializer<ColorConfig> {

        @Override
        public ColorConfig deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();

            // Deserialize the interpolant
            MolangExpression interpolant = context.deserialize(jsonObject.get("interpolant"), MolangExpression.class);

            List<ColorConfig.GradientStop> gradientStops = new ArrayList<>();

            JsonElement gradientElement = jsonObject.get("gradient");

            // Check if gradient is an array or an object
            if (gradientElement.isJsonArray()) {
                // Handle array format for colors
                JsonArray gradientArray = gradientElement.getAsJsonArray();
                int numColors = gradientArray.size();
                for (int i = 0; i < numColors; i++) {
                    JsonElement colorElement = gradientArray.get(i);
                    float position = (float) i / (numColors - 1); // Distribute positions from 0 to 1
                    int color = parseColor(colorElement);
                    gradientStops.add(new ColorConfig.GradientStop(position, color));
                }
            } else if (gradientElement.isJsonObject()) {
                // Handle object format for gradient
                JsonObject gradientJson = gradientElement.getAsJsonObject();
                for (Map.Entry<String, JsonElement> entry : gradientJson.entrySet()) {
                    try {
                        float position = Float.parseFloat(entry.getKey());
                        int color = parseColor(entry.getValue());
                        gradientStops.add(new ColorConfig.GradientStop(position, color));
                    } catch (NumberFormatException e) {
                        throw new JsonParseException("Invalid gradient position: " + entry.getKey(), e);
                    }
                }
                gradientStops.sort(Comparator.comparing(ColorConfig.GradientStop::position));
            } else {
                throw new JsonParseException("Invalid gradient format: must be either an array or an object.");
            }

            // Create and populate ColorConfig
            ColorConfig colorConfig = new ColorConfig();
            colorConfig.gradient = gradientStops;
            colorConfig.interpolant = interpolant;

            return colorConfig;
        }

        private int parseColor(JsonElement colorElement) {
            if (colorElement.isJsonPrimitive()) {
                return ColorUtil.parseHexColor(colorElement.getAsString());
            } else if (colorElement.isJsonArray()) {
                JsonArray colorArray = colorElement.getAsJsonArray();
                float r = colorArray.get(0).getAsFloat();
                float g = colorArray.get(1).getAsFloat();
                float b = colorArray.get(2).getAsFloat();
                float a = colorArray.size() > 3 ? colorArray.get(3).getAsFloat() : 1.0f;
                return ColorUtil.fromRGBA(r, g, b, a);
            } else {
                throw new JsonParseException("Invalid color format: must be a hex string or array.");
            }
        }
    }
}
