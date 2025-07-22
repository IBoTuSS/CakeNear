package dev.cakestudio.cakenear;

import dev.cakestudio.cakenear.command.NearCommand;
import dev.cakestudio.cakenear.config.ConfigManager;
import dev.cakestudio.cakenear.util.HexColor;
import lombok.Getter;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class CakeNear extends JavaPlugin {

    @Getter
    private LuckPerms luckPerms;

    private void msg(String msg) {
        String prefix = "&#C102FACakeNear &7| ";
        Bukkit.getConsoleSender().sendMessage(HexColor.deserialize(prefix + msg));
    }

    @Override
    public void onEnable() {
        ConfigManager.loadYaml(this);
        RegisteredServiceProvider<LuckPerms> provider = getServer().getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            luckPerms = provider.getProvider();
        } else {
            getLogger().severe("LuckPerms не найден!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        getCommand("near").setExecutor(new NearCommand(luckPerms));
        Bukkit.getConsoleSender().sendMessage("");
        msg("&fDeveloper: &#C102FACakeStudio");
        msg("&fVersion: &#C102FAv" + getDescription().getVersion());
        Bukkit.getConsoleSender().sendMessage("");
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage("");
        msg("&fDisable plugin.");
        Bukkit.getConsoleSender().sendMessage("");
    }
}
