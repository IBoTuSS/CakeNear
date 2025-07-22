package dev.cakestudio.cakenear.command;

import dev.cakestudio.cakenear.config.ConfigManager;
import dev.cakestudio.cakenear.near.LuckPermsManager;
import dev.cakestudio.cakenear.near.NearManager;
import dev.cakestudio.cakenear.util.HexColor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.luckperms.api.LuckPerms;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;

@RequiredArgsConstructor
public class NearCommand implements CommandExecutor {

    private final LuckPerms luckPerms;

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, @NonNull String[] args) {
        FileConfiguration config = ConfigManager.getConfig();
        if (!(sender instanceof Player player)) {
            sender.sendMessage(HexColor.deserialize(config.getString("messages.only-player")));
            return true;
        }

        if (!player.hasPermission("cakenear.near")) {
            player.sendMessage(HexColor.deserialize(config.getString("messages.no-permission")));
            return true;
        }

        LuckPermsManager luckPermsManager = new LuckPermsManager(luckPerms);

        int maxDistance = luckPermsManager.getPlayerNearDistance(player);

        List<Player> nearbyPlayers = player
                .getWorld()
                .getPlayers()
                .stream()
                .filter(p -> !p.equals(player) && p.getLocation().distance(player.getLocation()) <= maxDistance)
                .toList();

        if (nearbyPlayers.isEmpty()) {
            player.sendMessage(HexColor.deserialize(config.getString("messages.no-players-near")));
            return true;
        }

        player.sendMessage(HexColor.deserialize(config.getString("messages.player-near")));

        for (Player nearbyPlayer : nearbyPlayers) {
            int distance = (int) player.getLocation().distance(nearbyPlayer.getLocation());
            String directionArrow = NearManager.getDirection(player, nearbyPlayer);
            String format = config.getString("messages.player-near-format");

            Component playerComponent = Component.text(nearbyPlayer.getName())
                    .hoverEvent(HoverEvent.showText(
                            HexColor.deserialize(config.getString("messages.inventory-button-hover-text"))
                    ))
                    .clickEvent(ClickEvent.runCommand(
                            config.getString("messages.inventory-button-click-command")
                                    .replace("{player}", nearbyPlayer.getName())
                    ));

            String processedFormat = format
                    .replace("{distance}", String.valueOf(distance))
                    .replace("{arrows}", directionArrow);

            Component finalMessage = HexColor.deserialize(processedFormat)
                    .replaceText(builder -> builder
                            .matchLiteral("{player}")
                            .replacement(playerComponent)
                    );

            player.sendMessage(finalMessage);
        }

        return true;
    }
}
