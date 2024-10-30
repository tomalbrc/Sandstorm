package de.tomalbrc.heatwave.util;

public class ColorUtil {
    public static int parseHexColor(String hexColor) {
        hexColor = hexColor.replace("#", "");
        return (int) Long.parseLong(hexColor, 16);
    }
}