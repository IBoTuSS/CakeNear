package dev.cakestudio.cakenear.near;

import lombok.NonNull;
import org.bukkit.entity.Player;

public interface PlayerGroupProvider {
    @NonNull String getPrimaryGroup(@NonNull Player player);
}
