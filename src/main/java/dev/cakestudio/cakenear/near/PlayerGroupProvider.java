package dev.cakestudio.cakenear.near;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface PlayerGroupProvider {
    @NotNull String getPrimaryGroup(@NotNull Player player);
}
