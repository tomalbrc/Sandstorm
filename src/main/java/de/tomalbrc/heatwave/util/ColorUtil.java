package de.tomalbrc.heatwave.util;

public class ColorUtil {
    public static int parseHexColor(String hexColor) {
        hexColor = hexColor.replace("#", "");
        if (hexColor.length() == 6)
            hexColor = "ff"+hexColor;
        return (int) Long.parseLong(hexColor, 16);
    }
}