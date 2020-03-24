package me.freedy.event;

import me.freedy.BetterDeath;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class onWorld implements Listener {
    private BetterDeath plugin;
    public onWorld(BetterDeath plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onWorld(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        if (event.getPlayer().getWorld().toString() == "world") {
            String playerpath = "level." + player.getUniqueId().toString();
            plugin.getConfig().set(playerpath, 10);
            Bukkit.getScheduler().runTaskLater(plugin, () -> plugin.giveKit(player, 10), 10);
        }
    }
}
