package dev.cakestudio.cakenear.command;

import dev.cakestudio.cakenear.manager.AutoNearManager;
import dev.cakestudio.cakenear.manager.CooldownManager;
import dev.cakestudio.cakenear.manager.NearManager;
import dev.cakestudio.cakenear.luckperms.PlayerGroupProvider;
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
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class NearCommand implements CommandExecutor, TabCompleter {

    private final CooldownManager cooldownManager;
    private final SettingsManager settings;
    private final PlayerGroupProvider groupProvider;
    private final AutoNearManager autoNearManager;

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, @NonNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(settings.getMessage("messages.only-player"));
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("auto")) {
            if (!player.hasPermission("cakenear.auto")) {
                player.sendMessage(settings.getMessage("messages.no-permission-autonear"));
                return true;
            }
            autoNearManager.toggleAutoNear(player);
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

    @Override
    public List<String> onTabComplete(@NonNull CommandSender sender, @NonNull Command command, @NonNull String alias, @NonNull String @NonNull [] args) {
        if (args.length == 1) {
            if (sender.hasPermission("cakenear.auto")) {
                List<String> completions = new ArrayList<>();
                completions.add("auto");
                return StringUtil.copyPartialMatches(args[0], completions, new ArrayList<>());
            }
        }
        return Collections.emptyList();
    }
}