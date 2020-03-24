package me.freedy.event;

import me.freedy.BetterDeath;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class onJoin implements Listener {
    private BetterDeath plugin;
    public onJoin(BetterDeath plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String playerpath = "level." + player.getUniqueId().toString();
        plugin.getConfig().set(playerpath, 10);
        player.teleport(Bukkit.getServer().getWorld("world").getSpawnLocation());
        Bukkit.getScheduler().runTaskLater(plugin, () -> plugin.giveKit(player, 10), 10);
    }
}
