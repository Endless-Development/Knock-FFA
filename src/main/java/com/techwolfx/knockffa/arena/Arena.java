package com.techwolfx.knockffa.arena;

import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.techwolfx.knockffa.Knockffa;
import com.techwolfx.knockffa.data.ArenaPlayer;
import com.techwolfx.knockffa.data.TempBlock;
import com.techwolfx.knockffa.events.ArenaJoinEvent;
import com.techwolfx.knockffa.events.ArenaLeftEvent;
import com.techwolfx.knockffa.utils.ChatUtils;
import com.techwolfx.knockffa.utils.Utils;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Arena {

    private static final Knockffa plugin = Knockffa.getInstance();

    @Getter
    private final int id;
    @Getter
    private Location spawnLocation;
    @Getter
    private final List<ArenaPlayer> players = new ArrayList<>();
    @Getter
    private final int maxPlayers;
    //@Getter
    //private final ScoreboardArena scoreboardArena = new ScoreboardArena(this);
    @Getter
    private boolean enabled;

    public Arena(int id) {
        this.id = id;
        this.maxPlayers = 50;
        this.enabled = false;
        runCheckBlockTask();
    }

    public Arena(int id, int maxPlayers) {
        this.id = id;
        this.maxPlayers = maxPlayers;
        this.enabled = false;
        runCheckBlockTask();
    }

    public Arena(int id, int maxPlayers, Location spawnLocation, Location lobbyTop, Location lobbyBottom) {
        this.id = id;
        this.maxPlayers = maxPlayers;
        this.spawnLocation = spawnLocation;
        this.lobbyArea = new CuboidSelection(spawnLocation.getWorld(), lobbyTop, lobbyBottom);
        this.enabled = true;
        runCheckBlockTask();
    }

    private Selection lobbyArea = null;
    public void setLobby(Location spawnLocation, Selection selection) {
        this.spawnLocation = spawnLocation;
        this.lobbyArea = selection;
        this.enabled = true;
        saveArenaToConfig();
    }

    private int task;
    @Getter
    public LinkedList<TempBlock> arenaBlockList = new LinkedList<>();

    private void runCheckBlockTask() {
        BukkitScheduler scheduler = Bukkit.getScheduler();

        // Remove all blocks (if present) and stop task
        if ( !enabled || players.size() < 1 ) {

            if (arenaBlockList.size() > 0) {
                for (TempBlock tempBlock : arenaBlockList) {
                    tempBlock.getLocation().getBlock().setType(Material.AIR);
                }
                arenaBlockList.clear();
            }

            scheduler.cancelTask(task);
            return;
        }

        if (scheduler.isCurrentlyRunning(task) || scheduler.isQueued(task)) {
            return;
        }

        task = scheduler.scheduleSyncRepeatingTask(plugin, () -> {
            if (arenaBlockList.isEmpty()) {
                return;
            }

            long currentMillis = System.currentTimeMillis();

            arenaBlockList.forEach(tempBlock -> {
                if (tempBlock.getLocation().getBlock().getType() == Material.AIR) {
                    tempBlock.setExpired(true);
                    return;
                }

                long secondsElapsed = TimeUnit.MILLISECONDS.toSeconds(currentMillis - tempBlock.getPlaceTime())+1;
                if (tempBlock.getFirstMaterial() == plugin.getConfigManager().getPlaceMaterial()) {
                    if (secondsElapsed == plugin.getConfig().getInt("blocks-expire-time.warning")) {
                        tempBlock.getLocation().getBlock().setType(plugin.getConfigManager().getExpiringMaterial());
                    } else if (secondsElapsed >= plugin.getConfig().getInt("blocks-expire-time.main")) {
                        tempBlock.getLocation().getBlock().setType(Material.AIR);
                        tempBlock.setExpired(true);
                    }
                } else {
                    // If not sandstone
                    if (secondsElapsed >= plugin.getConfig().getInt("blocks-expire-time.other")) {
                        tempBlock.getLocation().getBlock().setType(Material.AIR);
                        tempBlock.setExpired(true);
                    }
                }
            });

            arenaBlockList.removeIf(TempBlock::isExpired);
        }, 0L, 20L);
    }

    private void saveArenaToConfig() {
        plugin.getConfig().set("arenas."+id+".max-players", maxPlayers);
        plugin.getConfig().set("arenas."+id+".world", spawnLocation.getWorld().getName());

        plugin.getConfig().set("arenas."+id+".spawn.x", spawnLocation.getBlockX());
        plugin.getConfig().set("arenas."+id+".spawn.y", spawnLocation.getBlockY());
        plugin.getConfig().set("arenas."+id+".spawn.z", spawnLocation.getBlockZ());

        plugin.getConfig().set("arenas."+id+".lobby.top.x", lobbyArea.getMaximumPoint().getBlockX());
        plugin.getConfig().set("arenas."+id+".lobby.top.y", lobbyArea.getMaximumPoint().getBlockY());
        plugin.getConfig().set("arenas."+id+".lobby.top.z", lobbyArea.getMaximumPoint().getBlockZ());

        plugin.getConfig().set("arenas."+id+".lobby.bottom.x", lobbyArea.getMinimumPoint().getBlockX());
        plugin.getConfig().set("arenas."+id+".lobby.bottom.y", lobbyArea.getMinimumPoint().getBlockY());
        plugin.getConfig().set("arenas."+id+".lobby.bottom.z", lobbyArea.getMinimumPoint().getBlockZ());
        plugin.saveConfig();
    }

    public boolean isInLobby(Player p) {
        return lobbyArea.contains(p.getLocation());
    }

    public boolean isInLobby(Location location) {
        return lobbyArea.contains(location);
    }

    public boolean enable() {
        if (lobbyArea == null) {
            return false;
        }
        this.enabled = true;
        return true;
    }

    public void disable() {
        this.enabled = false;
    }

    /**
     * Broadcast a message to all players inside the arena.
     *
     * @param message the message to broadcast.
     */
    public void broadcast(String message) {
        players.forEach((arenaPlayer) -> {
            arenaPlayer.getPlayer().sendMessage(ChatUtils.colorMsg(message));
        });
    }

    /**
     * Checks if the arena is full or not.
     *
     * @return true if the arena is full.
     */
    public boolean isFull() {
        return players.size() >= maxPlayers;
    }

    /**
     * Adds a player to the Arena.
     *
     * @param player player to be added to the arena.
     */
    public void addPlayer(Player player) {
        ArenaPlayer newArenaPlayer = new ArenaPlayer(player, this.id);
        players.add(newArenaPlayer);
        //scoreboardArena.addPlayer(newArenaPlayer);

        setupPlayer(player);

        Bukkit.getPluginManager().callEvent(new ArenaJoinEvent(this, player));
        runCheckBlockTask();
    }

    /**
     * Teleport player to spawn and setup his inventory.
     * @param player the taregt player
     */
    public void setupPlayer(Player player) {
        player.teleport(this.spawnLocation);
        player.getInventory().setArmorContents(null);
        player.getInventory().clear();
        player.setGameMode(GameMode.SURVIVAL);
        player.setFoodLevel(20);
        player.setHealth(20);
        player.setLevel(0);
        player.setExp(0);
        player.setPlayerTime(0, false);
        player.setPlayerWeather(WeatherType.CLEAR);
        Utils.clearEffectFromPlayer(player);

        // Setup inventory
        player.getInventory().setArmorContents(Knockffa.getInstance().getConfigManager().getArmorItems());

        HashMap<Integer, ItemStack> items = Knockffa.getInstance().getConfigManager().getInventoryItems();
        items.forEach(((slot, item) -> {
            player.getInventory().setItem(slot, item);
        }));
    }

    /**
     * Removes a player from the Arena.
     *
     * @param player player to remove from the arena.
     */
    public void removePlayer(Player player) {

        if (players.removeIf(arenaPlayer -> arenaPlayer.getPlayer().equals(player))) {
            // If player removed successfully
            Bukkit.getPluginManager().callEvent(new ArenaLeftEvent(this, player));
            //scoreboardArena.removePlayer(player);
            player.getInventory().clear();
        }

        runCheckBlockTask();
    }

    public void kickAll() {
        players.forEach(arenaPlayer -> {
            arenaPlayer.getPlayer().getInventory().clear();
            Bukkit.getPluginManager().callEvent(new ArenaLeftEvent(this, arenaPlayer.getPlayer()));
        });

        players.clear();
        runCheckBlockTask();
    }

    public ArenaPlayer getArenaPlayer(Player p) {
        for (ArenaPlayer arenaPlayer : players) {
            if (arenaPlayer.getPlayer().equals(p)) {
                return arenaPlayer;
            }
        }
        return null;
    }

    public String toString() {
        return "ID " + getId() + "\n World " + getSpawnLocation().getWorld().getName() + "\n Max players: " + maxPlayers + "\n";
    }

}
