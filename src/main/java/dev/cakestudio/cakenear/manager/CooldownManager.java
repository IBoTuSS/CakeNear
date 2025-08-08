package dev.cakestudio.cakenear.manager;

import dev.cakestudio.cakenear.luckperms.PlayerGroupProvider;
import dev.cakestudio.cakenear.service.SettingsManager;
import lombok.NonNull;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class CooldownManager {

    private final SettingsManager settings;
    private final PlayerGroupProvider groupProvider;
    private final Map<UUID, Long> cooldowns = new ConcurrentHashMap<>();

    public CooldownManager(SettingsManager settings, PlayerGroupProvider groupProvider) {
        this.settings = settings;
        this.groupProvider = groupProvider;
    }

    private int getPlayerCooldownDuration(@NonNull Player player) {
        if (player.hasPermission("cakenear.bypass")) {
            return 0;
        }
        String playerGroup = groupProvider.getPrimaryGroup(player);
        return settings.getCooldown(playerGroup);
    }

    public void setCooldown(Player player) {
        int duration = getPlayerCooldownDuration(player);
        if (duration > 0) {
            cooldowns.put(player.getUniqueId(), System.currentTimeMillis());
        }
    }

    public boolean isOnCooldown(Player player) {
        return getRemainingTime(player) > 0;
    }

    public long getRemainingTime(Player player) {
        int durationSeconds = getPlayerCooldownDuration(player);
        if (durationSeconds == 0) return 0;

        long timeFromLastUse = System.currentTimeMillis() - cooldowns.getOrDefault(player.getUniqueId(), 0L);
        long remainingMillis = TimeUnit.SECONDS.toMillis(durationSeconds) - timeFromLastUse;

        return remainingMillis > 0 ? TimeUnit.MILLISECONDS.toSeconds(remainingMillis) + 1 : 0;
    }
}