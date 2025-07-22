package dev.cakestudio.cakenear.near;

import dev.cakestudio.cakenear.config.ConfigManager;
import lombok.RequiredArgsConstructor;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

/**
 * Класс-менеджер для взаимодействия с LuckPerms.
 * Отвечает за получение информации об игроках из LP.
 */
@RequiredArgsConstructor
public class LuckPermsManager {

    private final LuckPerms luckPerms;

    private String getPlayerPrimaryGroup(Player player) {
        User user = luckPerms.getUserManager().getUser(player.getUniqueId());
        if (user != null) {
            return user.getPrimaryGroup();
        }
        return "default";
    }

    public int getPlayerNearDistance(Player player) {
        FileConfiguration config = ConfigManager.getConfig();
        String group = getPlayerPrimaryGroup(player);

        String groupPath = "near-distance.groups." + group;

        return config.getInt(groupPath, config.getInt("near-distance.default", 100));
    }
}

