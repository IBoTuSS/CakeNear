package dev.cakestudio.cakenear.config;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class ConfigManager {
    @Getter
    private static FileConfiguration config;

    public static void loadYaml(@NotNull Plugin plugin) {
        File file = new File(plugin.getDataFolder(), "config.yml");
        if (!file.exists()) {
            plugin.saveResource("config.yml", true);
        }

        config = YamlConfiguration.loadConfiguration(file);
    }
}
