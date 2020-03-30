package freedy.freedyminigamemaker;

import freedy.freedyminigamemaker.commands.*;
import freedy.freedyminigamemaker.events.*;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public final class FreedyMinigameMaker extends JavaPlugin {


    private Map<String, BukkitTask> taskIDList = new HashMap<>();


    @Override
    public void onEnable() {
        getConfig().options().copyDefaults();
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(new QuitEvent(this), this);
        getServer().getPluginManager().registerEvents(new DeathEvent(this), this);
        getServer().getPluginManager().registerEvents(new MoveEvent(this), this);
        getCommand("fmg").setExecutor(new MinigameCommand(this));
    }

    @Override
    public void onDisable() {
        for (String s : getConfig().getStringList("gameList"))
            disableGame(s);
    }

    public void addPlayer(Player player, String gameName) {
        final int maxPlayers = getConfig().getInt("miniGames." + gameName + ".maxPlayers");
        boolean isPlaying = getConfig().getBoolean("miniGames." + gameName + ".isPlaying");
        List<String> playerNameList = getConfig().getStringList("miniGames." + gameName + ".players");
        String playerName = player.getName();


        if (!playerNameList.contains(playerName)) {
            if (playerNameList.size() < maxPlayers) {
                if (!isPlaying) {

                    playerNameList.add(playerName);
                    for (String s : playerNameList)
                        Bukkit.getPlayer(s).sendMessage("§a" + playerName + "이(가) " + gameName + "에 참여했어요!");
                    getConfig().set("miniGames." + gameName + ".players", playerNameList);
                    saveConfig();
                    startGame(gameName);

                } else player.sendMessage("§c" + "게임이 이미 시작되었습니다");
            } else player.sendMessage("§c" + "게임이 최대인원에 도달했습니다");
        } else player.sendMessage("§c" + "게임을 이미 플레이 중입니다");

    }

    final List<String> teamTypeList = Arrays.asList("default", "blue", "red");
    private void startGame(String gameName) {
        int i;
        final int maxStartPlayers = getConfig().getInt("miniGames." + gameName + ".maxStartPlayers");
        final int waitForStartTime = getConfig().getInt("miniGames." + gameName + ".waitForStartTime");
        final String gameType = getConfig().getString("miniGames." + gameName + ".gameType");
        final boolean teamMode = getConfig().getBoolean("miniGames." + gameName + ".teamMode");
        int timer = getConfig().getInt("miniGames." + gameName + ".waitTime");
        boolean isPlaying = getConfig().getBoolean("miniGames." + gameName + ".isPlaying");
        List<String> playerNameList = getConfig().getStringList("miniGames." + gameName + ".players");
        Map<String, Integer> teamStartLocationSize = new HashMap<>();
        Map<String, List<Location>> teamStartLocationList = new HashMap<>();
        for (String teamName : teamTypeList) {
            teamStartLocationSize.put(teamName, getConfig().getInt("miniGames." + gameName + "." + teamName + "StartLocationAmount"));
            teamStartLocationList.put(teamName, new ArrayList<>());
        }
        for (String team : teamTypeList) {
            for (int num = 1; num < teamStartLocationSize.get(team) + 1; num++) {
                World world = Bukkit.getWorld(getConfig().getString("miniGames." + gameName + "." + team + "startLocation." + num + ".world"));
                double x = Double.parseDouble(getConfig().getString("miniGames." + gameName + "." + team + "startLocation." + num + ".x"));
                double y = Double.parseDouble(getConfig().getString("miniGames." + gameName + "." + team + "startLocation." + num + ".y"));
                double z = Double.parseDouble(getConfig().getString("miniGames." + gameName + "." + team + "startLocation." + num + ".z"));
                float yaw = Float.parseFloat(getConfig().getString("miniGames." + gameName + "." + team + "startLocation." + num + ".yaw"));
                float pitch = Float.parseFloat(getConfig().getString("miniGames." + gameName + "." + team + "startLocation." + num + ".pitch"));
                teamStartLocationList.get(team).add(new Location(world, x, y, z, yaw, pitch));
                Collections.shuffle(teamStartLocationList.get(team));
            }
        }
        if (playerNameList.size() >= maxStartPlayers) {
            if (timer >= waitForStartTime) {
                isPlaying = true;
                timer = 0;
                i = 0;
                taskIDList.replace(gameName + "-starting", Bukkit.getScheduler().runTaskLater(this, () -> stopGame(gameName), 20));

                if (teamMode) {
                    Collections.shuffle(playerNameList);
                    i = 0;
                    List<String> redTeamPlayerNameList = getConfig().getStringList("miniGames." + gameName + ".redTeamPlayerList");
                    List<String> blueTeamPlayerNameList = getConfig().getStringList("miniGames." + gameName + ".blueTeamPlayerList");
                    for (String playerName : playerNameList) {
                        Player player = Bukkit.getPlayer(playerName);
                        player.sendMessage("§a" + "게임이 시작되었어요!");
                        if (i % 2 == 0) {
                            redTeamPlayerNameList.add(playerName);
                            player.teleport(teamStartLocationList.get("red").get(i));
                        }
                        else {
                            blueTeamPlayerNameList.add(playerName);
                            player.teleport(teamStartLocationList.get("blue").get(i));
                        }
                        i++;
                    }
                    getConfig().set("miniGames." + gameName + ".blueTeamPlayerList", blueTeamPlayerNameList);
                    getConfig().set("miniGames." + gameName + ".redTeamPlayerList", redTeamPlayerNameList);
                } else
                    for (String s : playerNameList) {
                        Player player = Bukkit.getPlayer(s);
                        player.sendMessage("§a" + "게임이 시작되었어요!");
                        player.teleport(teamStartLocationList.get("default").get(i));
                        i++;
                    }
                switch (gameType) {
                    case "hideAndSeek":
                        setBlockOnStart(playerNameList, gameName);
                        break;
                    case "zombieMode":
                        Collections.shuffle(playerNameList);
                        i = 0;
                        List<String> redTeamPlayerNameList = getConfig().getStringList("miniGames." + gameName + ".redTeamPlayerList");
                        List<String> blueTeamPlayerNameList = getConfig().getStringList("miniGames." + gameName + ".blueTeamPlayerList");
                        for (String s : playerNameList) {
                            if (i == 0) redTeamPlayerNameList.add(s);
                            else blueTeamPlayerNameList.add(s);
                            i++;
                        }
                        getConfig().set("miniGames." + gameName + ".blueTeamPlayerList", blueTeamPlayerNameList);
                        getConfig().set("miniGames." + gameName + ".redTeamPlayerList", redTeamPlayerNameList);
                        break;
                }
            } else {
                for (String s : playerNameList)
                    Bukkit.getPlayer(s).sendMessage("§7" + (waitForStartTime - timer) + "초후 게임 시작...");
                timer++;
                taskIDList.replace(gameName + "-starting", Bukkit.getScheduler().runTaskLater(this, () -> startGame(gameName), 20));
            }
        }

        getConfig().set("miniGames." + gameName + ".isPlaying", isPlaying);
        getConfig().set("miniGames." + gameName + ".waitTime", timer);
        saveConfig();
    }

    public void stopGame(String gameName) {
        final int waitForEndTime = getConfig().getInt("miniGames." + gameName + ".waitForEndTime");
        boolean isPlaying = getConfig().getBoolean("miniGames." + gameName + ".isPlaying");
        int timer = getConfig().getInt("miniGames." + gameName + ".waitTime");
        List<String> playerNameList = getConfig().getStringList("miniGames." + gameName + ".players");
        if (isPlaying) {
            if (timer >= waitForEndTime) {
                if (playerNameList.size() != 0) {
                    timer = 0;
                    for (String playerName : playerNameList) {
                        Player player = Bukkit.getPlayer(playerName);
                        player.sendMessage("§a" + "게임이 종료됩니다~!");
                    }
                    getConfig().set("miniGames." + gameName + ".players", playerNameList);
                    getConfig().set("miniGames." + gameName + ".isPlaying", false);
                    getConfig().set("miniGames." + gameName + ".waitTime", timer);
                    saveConfig();
                    disableGame(gameName);
                }
            } else {
                for (String s : playerNameList)
                    Bukkit.getPlayer(s).sendMessage("§7" + (waitForEndTime - timer) + "초후 게임 종료...");
                timer++;
                taskIDList.replace(gameName + "-stopping", Bukkit.getScheduler().runTaskLater(this, () -> stopGame(gameName), 20));
                getConfig().set("miniGames." + gameName + ".waitTime", timer);
                saveConfig();
            }

        }
    }
    private void disableGame(String gameName) {
        List<String> playerNameList = getConfig().getStringList("miniGames." + gameName + ".players");
        if (!playerNameList.isEmpty()) {
            for (String playerName : playerNameList) {
                removePlayer(playerName, gameName);
            }
        }
        getConfig().set("miniGames." + gameName + ".isPlaying", false);
        getConfig().set("miniGames." + gameName + ".waitTime", 0);
        saveConfig();
        if (!taskIDList.isEmpty()) {
            taskIDList.get(gameName + "-starting").cancel();
            taskIDList.get(gameName + "-stopping").cancel();
        }
    }
    public void removePlayer(String playerName, String gameName) {
        List<String> playerNameList = getConfig().getStringList("miniGames." + gameName + ".players");
        World world = Bukkit.getWorld(getConfig().getString("miniGames." + gameName + ".endLocation.world"));
        double x = Double.parseDouble(getConfig().getString("miniGames." + gameName + ".endLocation.x"));
        double y = Double.parseDouble(getConfig().getString("miniGames." + gameName + ".endLocation.y"));
        double z = Double.parseDouble(getConfig().getString("miniGames." + gameName + ".endLocation.z"));
        float yaw = Float.parseFloat(getConfig().getString("miniGames." + gameName + ".endLocation.yaw"));
        float pitch = Float.parseFloat(getConfig().getString("miniGames." + gameName + ".endLocation.pitch"));
        Location endLocation = new Location(world, x, y, z, yaw, pitch);
        Player player = Bukkit.getPlayer(playerName);

        for (String s : playerNameList)
            Bukkit.getPlayer(s).sendMessage("§6" + playerName + "이(가) 떠났습니다");

        player.teleport(endLocation);
        player.setGameMode(GameMode.valueOf(getConfig().getString("miniGames." + gameName + ".defaultEndGameMode")));

        playerNameList.remove(playerName);
        getConfig().set("miniGames." + gameName + ".players", playerNameList);
        saveConfig();
    }

    public void createGame(String gameName, int maxPlayers, int maxStartPlayers, int waitForStartTime, int waitForEndTime) { //<미니게임이름> <미니게임최대인원> <미니게임시작인원> <시작대기시간초> <게임종료시간초>
        getConfig().set("miniGames." + gameName + ".maxPlayers", maxPlayers);
        getConfig().set("miniGames." + gameName + ".maxStartPlayers", maxStartPlayers);
        getConfig().set("miniGames." + gameName + ".waitForStartTime", waitForStartTime);
        getConfig().set("miniGames." + gameName + ".waitForEndTime", waitForEndTime);
        getConfig().set("miniGames." + gameName + ".gameType", "death");
        getConfig().set("miniGames." + gameName + ".defaultStartGameMode", "ADVENTURE");
        getConfig().set("miniGames." + gameName + ".defaultEndGameMode", "ADVENTURE");
        List<String> gameList = getConfig().getStringList("gameList");
        gameList.add(gameName);
        getConfig().set("gameList", gameList);
        saveConfig();
    }

    public void setData(String path, Object dataType) {
        getConfig().set(path, dataType);
        saveConfig();
    }

    public void removeGame(String gameName) {
        List<String> gameList = getConfig().getStringList("gameList");
        gameList.remove(gameName);
        getConfig().set("miniGames." + gameName, null);
        getConfig().set("gameList", gameList);
        saveConfig();
    }

    public void setBlockOnStart(List<String> playerNameList, String gameName) {
        List<String> allowedBlocks = getConfig().getStringList("miniGames." + gameName + ".allowedBlocks");
        Collections.shuffle(allowedBlocks);
        int i = 0;
        for (String playerName : playerNameList) {
            String materialName = allowedBlocks.get(i);
            getConfig().set("miniGames." + gameName + ".playerData." + playerName + ".blockData", materialName);
            Bukkit.getPlayer(playerName).getLocation().getBlock().setType(Material.valueOf(materialName));
            getConfig().set("miniGames." + gameName + ".playerData." + playerName + ".backup", "AIR");
            i++;
        }
        saveConfig();
    }
}