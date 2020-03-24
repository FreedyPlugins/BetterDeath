package me.freedy.event;

import me.freedy.BetterDeath;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

public class onDeath {

    private BetterDeath plugin;

    public onDeath(BetterDeath plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity().getPlayer();
        Entity killer = player.getKiller();
        String playerpath = "level." + player.getUniqueId().toString();
        Integer playerlevel = plugin.getConfig().getInt(playerpath);
        if (killer instanceof Player) {
            Player playerkiller = (Player) killer;
            String killerpath = "level." + playerkiller.getUniqueId().toString();
            Integer killerlevel = plugin.getConfig().getInt(killerpath);
            plugin.deathSystem(player, playerkiller, playerlevel, killerlevel);
            playerlevel = plugin.playerSystem(player, playerlevel);
            killerlevel = plugin.killerSystem(playerkiller, killerlevel);
            plugin.getConfig().set(killerpath, killerlevel);
            plugin.getConfig().set(playerpath, playerlevel);
            plugin.saveConfig();
        }else {
            playerlevel = plugin.playerSystem(player, playerlevel);
            plugin.deathMotion(player, playerlevel, null);
            plugin.getConfig().set(playerpath, playerlevel);
            plugin.saveConfig();
        }
    }
}
