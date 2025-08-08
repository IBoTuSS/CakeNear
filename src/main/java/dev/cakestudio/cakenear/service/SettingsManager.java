package dev.cakestudio.cakenear.service;

import dev.cakestudio.cakenear.util.HexColor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;

@RequiredArgsConstructor
public class SettingsManager {

    private final FileConfiguration config;

    public int getNearDistance(String group) {
        return config.getInt("near-distance.groups." + group, config.getInt("near-distance.default"));
    }

    public int getCooldown(String group) {
        return config.getInt("command-cooldown.groups." + group, config.getInt("command-cooldown.default"));
    }

    public boolean isShowInvisiblePlayers() {
        return config.getBoolean("options.show-invisible-players");
    }

    public boolean isShowSpectators() {
        return config.getBoolean("options.show-spectators");
    }

    public List<String> getDisabledWorlds() {
        return config.getStringList("options.disabled-worlds");
    }

    public String getArrow(String direction) {
        return config.getString("arrows." + direction);
    }

    public int getAutoNearInterval() {
        return config.getInt("near-auto.interval");
    }

    public boolean isAutoNearDisableOnDamageEnabled() {
        return config.getBoolean("near-auto.disable-on-damage");
    }

    public Component getMessage(String key) {
        return HexColor.deserialize(getRawString(key));
    }

    public Component getFormattedMessage(String key, String @NonNull ... replacements) {
        String message = getRawString(key);
        for (int i = 0; i < replacements.length; i += 2) {
            message = message.replace(replacements[i], replacements[i + 1]);
        }
        return HexColor.deserialize(message);
    }

    public String getClickCommand(@NonNull Player player) {
        return getRawString("messages.inventory-button-click-command")
                .replace("{player}", player.getName());
    }

    public String getClickPermission() {
        return config.getString("messages.inventory-button-permission");
    }

    public Component getHoverText() {
        return getMessage("messages.inventory-button-hover-text");
    }

    public String getRawString(String key) {
        return config.getString(key);
    }
}