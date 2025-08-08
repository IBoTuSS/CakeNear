package dev.cakestudio.cakenear.listener;

import dev.cakestudio.cakenear.manager.AutoNearManager;
import dev.cakestudio.cakenear.service.SettingsManager;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

@RequiredArgsConstructor
public class PlayerDamageListener implements Listener {

    private final AutoNearManager autoNearManager;
    private final SettingsManager settings;

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (!settings.isAutoNearDisableOnDamageEnabled()) {
            return;
        }

        if (event.getEntity() instanceof Player damagedPlayer) {
            if (event.getDamager() instanceof Player) {
                autoNearManager.disableAutoNearOnDamage(damagedPlayer);
            }
        }

        if (event.getDamager() instanceof Player damagerPlayer) {
            if (event.getEntity() instanceof Player) {
                autoNearManager.disableAutoNearOnDamage(damagerPlayer);
            }
        }
    }
}