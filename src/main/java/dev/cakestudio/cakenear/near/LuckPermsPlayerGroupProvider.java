package dev.cakestudio.cakenear.near;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class LuckPermsPlayerGroupProvider implements PlayerGroupProvider {

    private final LuckPerms luckPerms;

    @Override
    public @NonNull String getPrimaryGroup(@NonNull Player player) {
        User user = this.luckPerms.getUserManager().getUser(player.getUniqueId());
        if (user != null) {
            String primaryGroup = user.getPrimaryGroup();
            if (primaryGroup != null) {
                return primaryGroup;
            }
        }
        return "default";
    }
}
