package dev.cakestudio.cakenear.manager;

import dev.cakestudio.cakenear.CakeNear;
import dev.cakestudio.cakenear.service.SettingsManager;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AutoNearManager {

    private final CakeNear plugin;
    private final SettingsManager settings;
    private final Map<UUID, BukkitTask> autoNearTasks = new ConcurrentHashMap<>();

    public AutoNearManager(CakeNear plugin, SettingsManager settings) {
        this.plugin = plugin;
        this.settings = settings;
    }

    public void toggleAutoNear(Player player) {
        if (isAutoNearEnabled(player)) {
            disableAutoNear(player, false);
        } else {
            enableAutoNear(player);
        }
    }

    private void enableAutoNear(@NonNull Player player) {
        int intervalSeconds = settings.getAutoNearInterval();
        long intervalTicks = 20L * intervalSeconds;

        BukkitTask task = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            if (!player.isOnline() || !isAutoNearEnabled(player)) {
                disableAutoNear(player, true);
                return;
            }
            Bukkit.dispatchCommand(player, "near");
        }, 0L, intervalTicks);

        autoNearTasks.put(player.getUniqueId(), task);
        player.sendMessage(settings.getMessage("messages.auto-near-enabled"));
    }

    public void disableAutoNear(@NonNull Player player, boolean silent) {
        BukkitTask task = autoNearTasks.remove(player.getUniqueId());
        if (task != null) {
            task.cancel();
            if (!silent) {
                player.sendMessage(settings.getMessage("messages.auto-near-disabled"));
            }
        }
    }

    public void disableAutoNearOnDamage(Player player) {
        if(isAutoNearEnabled(player)) {
            disableAutoNear(player, true);
            player.sendMessage(settings.getMessage("messages.auto-near-disabled-by-damage"));
        }
    }

    public boolean isAutoNearEnabled(@NonNull Player player) {
        return autoNearTasks.containsKey(player.getUniqueId());
    }

    public void disableAll() {
        autoNearTasks.values().forEach(BukkitTask::cancel);
        autoNearTasks.clear();
    }
}