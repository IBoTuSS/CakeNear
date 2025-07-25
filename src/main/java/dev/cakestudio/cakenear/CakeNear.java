package dev.cakestudio.cakenear;

import dev.cakestudio.cakenear.command.NearCommand;
import dev.cakestudio.cakenear.near.CooldownManager;
import dev.cakestudio.cakenear.near.LuckPermsPlayerGroupProvider;
import dev.cakestudio.cakenear.near.PlayerGroupProvider;
import dev.cakestudio.cakenear.service.SettingsManager;
import dev.cakestudio.cakenear.util.HexColor;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class CakeNear extends JavaPlugin {

    private void msg(String msg) {
        String prefix = "&#C102FACakeNear &7| ";
        Bukkit.getConsoleSender().sendMessage(HexColor.deserialize(prefix + msg));
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        FileConfiguration config = getConfig();

        RegisteredServiceProvider<LuckPerms> provider = getServer().getServicesManager().getRegistration(LuckPerms.class);
        if (provider == null) {
            getLogger().severe("LuckPerms не найден!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        LuckPerms luckPerms = provider.getProvider();

        SettingsManager settingsManager = new SettingsManager(config);
        PlayerGroupProvider groupProvider = new LuckPermsPlayerGroupProvider(luckPerms);
        CooldownManager cooldownManager = new CooldownManager(settingsManager, groupProvider);

        PluginCommand nearCommand = getCommand("near");
        if (nearCommand != null) {
            nearCommand.setExecutor(new NearCommand(cooldownManager, settingsManager, groupProvider));
        } else {
            getLogger().severe("Команда 'near' не найдена в plugin.yml!");
        }

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
