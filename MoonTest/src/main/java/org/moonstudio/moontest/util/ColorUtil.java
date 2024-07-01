package org.moonstudio.moontest.util;

import org.bukkit.ChatColor;

public class ColorUtil {
    public static String colorAlternate(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
