package me.freedy.event;

import me.freedy.BetterDeath;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class onQuit implements Listener {
    private BetterDeath plugin;
    public onQuit(BetterDeath plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        player.teleport(Bukkit.getServer().getWorld("world").getSpawnLocation());
        plugin.removeUUID(event.getPlayer());
    }
}
