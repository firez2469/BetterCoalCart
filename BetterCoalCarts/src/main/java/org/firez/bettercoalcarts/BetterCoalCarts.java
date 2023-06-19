package org.firez.bettercoalcarts;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public final class BetterCoalCarts extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getLogger().info(ChatColor.GREEN+"The server has been touched by the fine graces of "+this.getName());;
        this.getServer().getPluginManager().registerEvents(new MinecartEventListener(),this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
