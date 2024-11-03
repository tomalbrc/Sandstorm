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
            MolangExpression interpolant = context.deserialize(jsonObject.get("interpolant"), MolangExpression.class);

            List<ColorConfig.GradientStop> gradientStops = new ArrayList<>();

            JsonObject gradientJson = jsonObject.getAsJsonObject("gradient");
            for (Map.Entry<String, JsonElement> entry : gradientJson.entrySet()) {
                try {
                    float position = Float.parseFloat(entry.getKey());
                    int color = ColorUtil.parseHexColor(entry.getValue().getAsString());
                    gradientStops.add(new ColorConfig.GradientStop(position, color));
                } catch (NumberFormatException e) {
                    throw new JsonParseException("Invalid gradient position: " + entry.getKey(), e);
                }
            }

            gradientStops.sort(Comparator.comparing(ColorConfig.GradientStop::position));

            ColorConfig colorConfig = new ColorConfig();
            colorConfig.gradient = gradientStops;
            colorConfig.interpolant = interpolant;

            return colorConfig;
        }
    }
}
