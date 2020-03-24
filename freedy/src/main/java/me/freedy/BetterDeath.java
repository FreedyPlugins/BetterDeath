package me.freedy;

import me.freedy.event.onDamage;
import me.freedy.event.onJoin;
import me.freedy.event.onQuit;
import me.freedy.event.onWorld;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class BetterDeath extends JavaPlugin implements Listener{

    private static Economy econ = null;


    @Override
    public void onEnable() {
        if (!setupEconomy() ) {
            System.out.println("§cNo Economy plugin Found Disabling Vault");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        getConfig().options().copyDefaults();
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new onDamage(this), this);
        getServer().getPluginManager().registerEvents(new onJoin(this), this);
        getServer().getPluginManager().registerEvents(new onQuit(this), this);
        getServer().getPluginManager().registerEvents(new onWorld(this), this);
    }
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
    private static Economy getEconomy() {
        return econ;
    }


    public void deathMotion(Player player, Integer level, Player killer) {
        player.setHealth(player.getHealthScale());
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_DEATH, 1.0F, 1.0F);
        player.setGameMode(GameMode.SPECTATOR);
        player.sendTitle("§c죽었습니다!", "§7잠시후 리스폰 됩니다", 0, 60, 0);
        Bukkit.getScheduler().runTaskLater(this, () -> player.teleport(player.getWorld().getSpawnLocation()), 100);
        Bukkit.getScheduler().runTaskLater(this, () -> player.setGameMode(GameMode.ADVENTURE), 100);
        Bukkit.getScheduler().runTaskLater(this, () -> giveKit(player, level), 100);
        if (killer != null)
            killer.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
    }
    public Integer playerSystem(Player player, Integer playerlevel) {
        if (playerlevel == null || playerlevel > 10)
            playerlevel = 10;
        else if (playerlevel <= 10 && playerlevel > 5)
            playerlevel--;
        if (playerlevel == 7)
            player.sendMessage("§6뉴비모드 ON~!");
        return playerlevel;
    }
    public Integer killerSystem(Player killer, Integer killerlevel) {
        if (killerlevel == null)
            killerlevel = 10;
        else if (killerlevel < 8) {
            killerlevel = 9;
            killer.sendMessage("§6뉴비모드 OFF~!");
        } else if (killerlevel < 10 && killerlevel > 7)
            killerlevel = 10;
        killerlevel++;

        Economy eco = getEconomy();
        double money = 0.0;
        switch (killerlevel) {
            case 5:
                break;
            case 6:
                break;
            case 7:
                break;
            case 8:
                killer.sendMessage("§a킬! +200코인");
                money = 200.0;
                break;
            case 9:
                killer.sendMessage("§a킬! +200코인");
                money = 200.0;
                break;
            case 10:
                killer.sendMessage("§a킬! +200코인");
                money = 200.0;
                break;
            case 11:
                killer.sendMessage("§a킬! +200코인");
                money = 200.0;
                break;
            case 12:
                killer.sendMessage("§a두번 킬! +200코인");
                money = 200.0;
                break;
            case 13:
                killer.sendMessage("§a세번 킬! +300코인");
                money = 300.0;
                break;
            case 14:
                killer.sendMessage("§a킬 때리고! +400코인");
                money = 400.0;
                break;
            case 15:
                killer.sendMessage("§a킬의 지배자! + 500코인");
                money = 500.0;
                break;
            case 16:
                killer.sendMessage("§a엄청 킬! +700코인");
                money = 700.0;
                break;
            case 17:
                killer.sendMessage("§a멈출 수가 없지! +800코인");
                money = 800.0;
                break;
            case 18:
                killer.sendMessage("§a킬의 고수! +1000코인");
                money = 1000.0;
                break;
            case 19:
                killer.sendMessage("§a괴물같은 킬! +1500코인");
                money = 1500.0;
                break;
            case 20:
                killer.sendMessage("§c킬의 신! +2000코인");
                money = 2000.0;
                break;
            default:
                killer.sendMessage("§c신을 넘어섰드아아아아! +3000코인");
                money = 3000.0;
        }
        eco.depositPlayer(killer, money);
        return killerlevel;
    }
    public void deathSystem(Player player, Player killer, Integer playerlevel, Integer killerlevel) {

        String killerPrefix, playerPrefix;
        switch (killerlevel) {
            case 5:
                killerPrefix = "트롤 ";
                break;
            case 6:
                killerPrefix = "늅늅 ";
                break;
            case 7:
                killerPrefix = "뉴비 ";
                break;
            case 8:
                killerPrefix = "병아리 ";
                break;
            case 9:
                killerPrefix = "병아리 ";
                break;
            case 10:
                killerPrefix = "병아리 ";
                break;
            case 11:
                killerPrefix = "꼬꼬닭 ";
                break;
            case 12:
                killerPrefix = "후라이드치킨 ";
                break;
            case 13:
                killerPrefix = "양념치킨 ";
                break;
            case 14:
                killerPrefix = "닭백숙 ";
                break;
            case 15:
                killerPrefix = "불닭볶음면 ";
                break;
            default:
                killerPrefix = "치킨마스터 ";
        } //set killerprefix
        switch (playerlevel) {
            case 5:
                playerPrefix = "트롤 ";
                break;
            case 6:
                playerPrefix = "늅늅 ";
                break;
            case 7:
                playerPrefix = "뉴비 ";
                break;
            case 8:
                playerPrefix = "병아리 ";
                break;
            case 9:
                playerPrefix = "병아리 ";
                break;
            case 10:
                playerPrefix = "병아리 ";
                break;
            case 11:
                playerPrefix = "꼬꼬닭 ";
                break;
            case 12:
                playerPrefix = "후라이드치킨 ";
                break;
            case 13:
                playerPrefix = "양념치킨 ";
                break;
            case 14:
                playerPrefix = "닭백숙 ";
                break;
            case 15:
                playerPrefix = "불닭볶음면 ";
                break;
            default:
                playerPrefix = "치킨마스터 ";
        } //set playerprefix
        Bukkit.getServer().broadcastMessage("§7§l" + playerPrefix + "§8§l" + player.getName() + "§7§l이(가) " + killerPrefix + "§8§l" + killer.getName() + "§7§l에게 살해당했습니다");

        giveKit(killer, killerlevel);
        deathMotion(player, playerlevel, killer);

    }

    public void giveKit(Player player, Integer level) {
        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "pvpkit " + level + " " + player.getName());
    }

    public void removeUUID(Player player) {
        String path = "level."+player.getUniqueId().toString();
        this.getConfig().set(path, null);
        this.saveConfig();
    }

}