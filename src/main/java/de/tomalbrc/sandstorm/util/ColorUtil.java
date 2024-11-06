package de.tomalbrc.sandstorm.util;

public class ColorUtil {
    public static int parseHexColor(String hexColor) {
        hexColor = hexColor.replace("#", "");
        if (hexColor.length() == 6)
            hexColor = "ff"+hexColor;
        return (int) Long.parseLong(hexColor, 16);
    }

    public static int fromRGBA(float r, float g, float b, float a) {
        int alpha = Math.round(a * 255);
        int red   = Math.round(r * 255);
        int green = Math.round(g * 255);
        int blue  = Math.round(b * 255);
        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }
}