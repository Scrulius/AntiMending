package com.knopp.antimending;

import com.knopp.antimending.listener.MendingListener;
import org.bukkit.plugin.java.JavaPlugin;

public class AntiMendingPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        // Registrar el listener de eventos
        getServer().getPluginManager().registerEvents(new MendingListener(this), this);
        getLogger().info("AntiMending ha sido habilitado. ¡Mending desactivado!");
    }

    @Override
    public void onDisable() {
        getLogger().info("AntiMending ha sido deshabilitado.");
    }
}
