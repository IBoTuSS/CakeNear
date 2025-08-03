package dev.cakestudio.cakenear.command;

import dev.cakestudio.cakenear.near.CooldownManager;
import dev.cakestudio.cakenear.near.NearManager;
import dev.cakestudio.cakenear.near.PlayerGroupProvider;
import dev.cakestudio.cakenear.service.SettingsManager;
import dev.cakestudio.cakenear.util.HexColor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

@RequiredArgsConstructor
public class NearCommand implements CommandExecutor {

    private final CooldownManager cooldownManager;
    private final SettingsManager settings;
    private final PlayerGroupProvider groupProvider;

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, @NonNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(settings.getMessage("messages.only-player"));
            return true;
        }

        List<String> disabledWorlds = settings.getDisabledWorlds();
        if (disabledWorlds.contains(player.getWorld().getName())) {
            player.sendMessage(settings.getMessage("messages.command-disabled-in-world"));
            return true;
        }

        if (!player.hasPermission("cakenear.near")) {
            player.sendMessage(settings.getMessage("messages.no-permission"));
            return true;
        }


        if (cooldownManager.isOnCooldown(player)) {
            long remaining = cooldownManager.getRemainingTime(player);
            player.sendMessage(settings.getFormattedMessage("messages.cooldown-message", "{time}", String.valueOf(remaining)));
            return true;
        }

        cooldownManager.setCooldown(player);

        String primaryGroup = groupProvider.getPrimaryGroup(player);
        int maxDistance = settings.getNearDistance(primaryGroup);
        boolean showInvisible = settings.isShowInvisiblePlayers();
        boolean showSpectators = settings.isShowSpectators();

        List<Player> nearbyPlayers = player.getWorld().getPlayers().stream()
                .filter(p -> !p.equals(player))
                .filter(p -> showInvisible || !p.hasPotionEffect(PotionEffectType.INVISIBILITY))
                .filter(p -> showSpectators || p.getGameMode() != GameMode.SPECTATOR)
                .filter(p -> p.getLocation().distanceSquared(player.getLocation()) <= (long) maxDistance * maxDistance)
                .toList();

        if (nearbyPlayers.isEmpty()) {
            player.sendMessage(settings.getMessage("messages.no-players-near"));
            return true;
        }

        player.sendMessage(settings.getMessage("messages.player-near"));

        for (Player nearbyPlayer : nearbyPlayers) {
            player.sendMessage(formatPlayerMessage(player, nearbyPlayer));
        }

        return true;
    }

    private @NonNull Component formatPlayerMessage(@NonNull Player source, @NonNull Player target) {
        int distance = (int) source.getLocation().distance(target.getLocation());
        String directionArrow = NearManager.getDirection(source, target, settings);

        String requiredPermission = settings.getClickPermission();
        boolean hasClickPermission = requiredPermission.isEmpty() || source.hasPermission(requiredPermission);

        Component playerComponent;

        if (hasClickPermission) {
            playerComponent = Component.text(target.getName())
                    .hoverEvent(HoverEvent.showText(settings.getHoverText()))
                    .clickEvent(ClickEvent.runCommand(settings.getClickCommand(target)));
        } else {
            playerComponent = Component.text(target.getName())
                    .hoverEvent(HoverEvent.showText(settings.getMessage("messages.inventory-button-hover-text-no-permission")));
        }

        String format = settings.getRawString("messages.player-near-format");

        String replacedFormat = format
                .replace("{distance}", String.valueOf(distance))
                .replace("{arrows}", directionArrow);

        return HexColor.deserialize(replacedFormat)
                .replaceText(builder -> builder.matchLiteral("{player}").replacement(playerComponent));
    }
}