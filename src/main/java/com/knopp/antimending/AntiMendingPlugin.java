package com.knopp.antimending;

import com.knopp.antimending.listener.MendingListener;
import org.bukkit.plugin.java.JavaPlugin;

public class AntiMendingPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        // Register the event listener
        getServer().getPluginManager().registerEvents(new MendingListener(this), this);
        getLogger().info("AntiMending has been enabled. Mending disabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("AntiMending has been disabled.");
    }
}
