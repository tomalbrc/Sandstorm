package de.tomalbrc.heatwave.component.misc;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.heatwave.util.ColorUtil;
import gg.moonflower.molangcompiler.api.MolangExpression;
import gg.moonflower.molangcompiler.api.MolangRuntime;
import gg.moonflower.molangcompiler.api.exception.MolangRuntimeException;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

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
        return getInterpolatedColor(gradient, interpolantValue);
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

        float t = (variableRainbow - lowerStop.position) / (upperStop.position - lowerStop.position);

        int lowerRed = (lowerStop.color >> 16) & 0xFF;
        int lowerGreen = (lowerStop.color >> 8) & 0xFF;
        int lowerBlue = lowerStop.color & 0xFF;

        int upperRed = (upperStop.color >> 16) & 0xFF;
        int upperGreen = (upperStop.color >> 8) & 0xFF;
        int upperBlue = upperStop.color & 0xFF;

        int red = (int) (lowerRed + t * (upperRed - lowerRed));
        int green = (int) (lowerGreen + t * (upperGreen - lowerGreen));
        int blue = (int) (lowerBlue + t * (upperBlue - lowerBlue));

        return (red << 16) | (green << 8) | blue;
    }
}
