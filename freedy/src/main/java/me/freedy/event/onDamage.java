package me.freedy.event;

import me.freedy.BetterDeath;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class onDamage implements Listener {
    private BetterDeath plugin;

    public onDamage(BetterDeath plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {

        Entity entity = event.getEntity();
        Entity damager = event.getDamager();
        if (entity instanceof Player) {
            Player player = ((Player) entity).getPlayer();
            if (player.getWorld().getName().equalsIgnoreCase("world") && player.getHealth() <= event.getDamage()) {
                event.setCancelled(true);
                String playerpath = "level." + player.getUniqueId().toString();
                if (damager instanceof Projectile) {
                    Entity shooter = (Entity) ((Projectile) damager).getShooter();

                    if (shooter instanceof Player) {
                        Player playerShooter = (Player) shooter;

                        Integer playerlevel = plugin.getConfig().getInt(playerpath);
                        String killerpath = "level." + playerShooter.getUniqueId().toString();
                        Integer killerlevel = plugin.getConfig().getInt(killerpath);
                        plugin.deathSystem(player, playerShooter, playerlevel, killerlevel);
                        playerlevel = plugin.playerSystem(player, playerlevel);
                        killerlevel = plugin.killerSystem(playerShooter, killerlevel);
                        plugin.getConfig().set(killerpath, killerlevel);
                        plugin.getConfig().set(playerpath, playerlevel);
                        plugin.saveConfig();
                    }


                }else if (damager instanceof Player) {
                    Player playerDamager = (Player) damager;
                    Integer playerlevel = plugin.getConfig().getInt(playerpath);
                    String killerpath = "level." + damager.getUniqueId().toString();
                    Integer killerlevel = plugin.getConfig().getInt(killerpath);
                    plugin.deathSystem(player, playerDamager, playerlevel, killerlevel);
                    playerlevel = plugin.playerSystem(player, playerlevel);
                    killerlevel = plugin.killerSystem(playerDamager, killerlevel);
                    plugin.getConfig().set(killerpath, killerlevel);
                    plugin.getConfig().set(playerpath, playerlevel);
                    plugin.saveConfig();
                }else {
                    Integer playerlevel = plugin.getConfig().getInt(playerpath);
                    playerlevel = plugin.playerSystem(player, playerlevel);
                    plugin.deathMotion(player, playerlevel, null);
                    plugin.getConfig().set(playerpath, playerlevel);
                    plugin.saveConfig();

                }
            }
        }
    }
}
