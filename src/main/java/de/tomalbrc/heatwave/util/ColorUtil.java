package de.tomalbrc.heatwave.util;

public class ColorUtil {
    public static int hslToRgb(float h, float s, float l) {
        float r, g, b;

        if (s == 0f) {
            r = g = b = l; // achromatic
        } else {
            float q = l < 0.5f ? l * (1 + s) : l + s - l * s;
            float p = 2 * l - q;
            r = hueToRgb(p, q, h + 1f / 3f);
            g = hueToRgb(p, q, h);
            b = hueToRgb(p, q, h - 1f / 3f);
        }
        return to255(r) << 16 | to255(g) << 8 | to255(b);
    }

    public static int to255(float v) {
        return (int) Math.min(255, 256 * v);
    }

    /**
     * Helper method that converts hue to rgb
     */
    public static float hueToRgb(float p, float q, float t) {
        if (t < 0f)
            t += 1f;
        if (t > 1f)
            t -= 1f;
        if (t < 1f / 6f)
            return p + (q - p) * 6f * t;
        if (t < 1f / 2f)
            return q;
        if (t < 2f / 3f)
            return p + (q - p) * (2f / 3f - t) * 6f;
        return p;
    }

    public static int parseHexColor(String hexColor) {
        // Remove the '#' if it exists
        hexColor = hexColor.replace("#", "");

        // Parse hex to an integer
        int colorInt = (int) Long.parseLong(hexColor, 16);

        // If the hex is in RGB format (6 characters), add full opacity (alpha = 255)
        if (hexColor.length() == 6) {
            colorInt |= 0xFF000000; // Add alpha channel (255) at the highest byte
        }

        return colorInt;
    }
}