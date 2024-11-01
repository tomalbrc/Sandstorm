package de.tomalbrc.heatwave.component.misc;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.heatwave.util.ColorUtil;
import gg.moonflower.molangcompiler.api.MolangExpression;
import gg.moonflower.molangcompiler.api.MolangRuntime;
import gg.moonflower.molangcompiler.api.exception.MolangRuntimeException;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.util.Mth;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class ColorConfig {
    @SerializedName("gradient")
    public Map<String, String> gradient;
    @SerializedName("interpolant")
    public MolangExpression interpolant;

    public int color(MolangRuntime runtime) throws MolangRuntimeException {
        var interpolantValue = runtime.resolve(interpolant);
        return getInterpolatedColor(this.gradient, Mth.clamp(interpolantValue, 0.f,1.f));
    }

    record GradientStop(float position, int color) {}

    public int getInterpolatedColor(Map<String, String> gradient, float variableRainbow) {
        List<GradientStop> gradientStops = new ObjectArrayList<>();

        for (Map.Entry<String, String> entry : gradient.entrySet()) {
            float position = Float.parseFloat(entry.getKey());
            int color = ColorUtil.parseHexColor(entry.getValue());
            gradientStops.add(new GradientStop(position, color));
        }

        gradientStops.sort(Comparator.comparing(stop -> stop.position));

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

        int lowerColor = lowerStop.color;
        int upperColor = upperStop.color;

        int lowerAlpha = (lowerColor >> 24) & 0xFF;
        int lowerRed   = (lowerColor >> 16) & 0xFF;
        int lowerGreen = (lowerColor >> 8) & 0xFF;
        int lowerBlue  = lowerColor & 0xFF;

        int upperAlpha = (upperColor >> 24) & 0xFF;
        int upperRed   = (upperColor >> 16) & 0xFF;
        int upperGreen = (upperColor >> 8) & 0xFF;
        int upperBlue  = upperColor & 0xFF;

        int interpAlpha = (int) Mth.lerp(variableRainbow, lowerAlpha, upperAlpha);
        int interpRed   = (int) Mth.lerp(variableRainbow, lowerRed, upperRed);
        int interpGreen = (int) Mth.lerp(variableRainbow, lowerGreen, upperGreen);
        int interpBlue  = (int) Mth.lerp(variableRainbow, lowerBlue, upperBlue);

        return (interpAlpha << 24) | (interpRed << 16) | (interpGreen << 8) | interpBlue;
    }
}
