package org.moonstudio.moontest;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class Config {
    static {
        Main.instance.saveDefaultConfig();
        reloadConfig();
    }

    public static FileConfiguration config;

    public static void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(new File(Main.instance.getDataFolder(), "config.yml"));
    }
}
