package com.tavillecode.libra;

import com.tavillecode.libra.command.LibraCompleter;
import com.tavillecode.libra.command.LibraExecutor;
import com.tavillecode.libra.listener.PlayerCraftListener;
import com.tavillecode.libra.storage.impl.YamlConfigurationStorage;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

/**
 * @author Interface39
 * @version 1.0
 * @description: TODO
 * @date 2025/3/5 10:28
 */
public class Libra extends JavaPlugin {
    private static Libra plugin;
    private static YamlConfigurationStorage yml;

    @Override
    public void onEnable() {
        plugin = this;

        yml = new YamlConfigurationStorage(this,this.getDataFolder(),"recipes",true,true);
        yml.reload();

        Objects.requireNonNull(getCommand("libra")).setExecutor(new LibraExecutor());
        Objects.requireNonNull(getCommand("libra")).setTabCompleter(new LibraCompleter());

        getServer().getPluginManager().registerEvents(new PlayerCraftListener(),this);
    }

    @Override
    public void onDisable() {

    }

    public static Libra getInstance() {
        return plugin;
    }

    public static YamlConfigurationStorage getYml() {
        return yml;
    }
}
