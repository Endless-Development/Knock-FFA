package com.techwolfx.knockffa.arena;

import com.techwolfx.knockffa.Knockffa;
import com.techwolfx.knockffa.data.ArenaPlayer;
import com.techwolfx.knockffa.utils.ChatUtils;
import com.techwolfx.knockffa.utils.ConsoleUtils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.*;

public class ArenaManager {

    private static final Knockffa plugin = Knockffa.getInstance();

    // Singleton instance
    private static ArenaManager am;

    // Arenas data
    @Getter
    private final List<Arena> arenas = new ArrayList<>();

    // Prevent instantiation
    private ArenaManager() {}

    // Singleton accessor
    public static ArenaManager getManager() {
        if (am == null)
            am = new ArenaManager();

        return am;
    }

    /**
     * Acquires an arena based on its ID number
     *
     * @param id the ID to search the arenas for
     * @return the arena possessing the specified ID
     */
    public Arena getArena(int id) {
        for (Arena a : this.arenas) {
            if (a.getId() == id) {
                return a;
            }
        }

        return null; // Not found
    }

    private Arena getFirstArena() {
        for (Arena a : this.arenas) {
            if (a.isEnabled() && !a.isFull()) {
                return a;
            }
        }

        return null; // Not found
    }

    public Arena getPlayerArena(Player player) {
        // Searches each arena for the player
        for (Arena a : this.arenas) {
            for (ArenaPlayer arenaPlayer : a.getPlayers()) {
                if (arenaPlayer.getPlayer().equals(player))
                    return a;
            }
        }

        return null;
    }

    /**
     * Checks if the player is currently in an arena
     *
     * @param player the player to check
     * @return {@code true} if the player is in game
     */
    public boolean isInArena(Player player) {
        for (Arena arena : this.arenas) {
            for (ArenaPlayer arenaPlayer : arena.getPlayers()) {
                if (arenaPlayer.getPlayer().equals(player))
                    return true;
            }
        }
        return false;
    }

    /**
     * Creates an arena
     *
     * @return the arena created
     */
    public Arena createArena(Player player) {
        Arena arena = new Arena(arenas.size() + 1);
        arenas.add(arena);

        player.sendMessage(ChatUtils.colorMsg("&aArena created (ID: "+(arenas.size()+1)+")"));

        return arena;
    }

    /**
     * Creates an arena
     *
     * @return the arena created
     */
    public Arena createArena(Player player, int maxPlayers) {
        Arena arena = new Arena(arenas.size() + 1, maxPlayers);
        arenas.add(arena);

        player.sendMessage(ChatUtils.colorMsg("&aArena created (ID: "+(arenas.size()+1)+")"));

        return arena;
    }

    /**
     * Adds the player to an arena
     *
     * <p>Gets the arena by ID, checks that it exists,
     * and check the player isn't already in a game.</p>
     *
     * @param p the player to add
     * @param id the arena ID. A check will be done to ensure its validity.
     */
    public void addPlayerToArena(Player p, int id) {
        if (this.isInArena(p)) {
            p.sendMessage(ChatUtils.colorMsg("&cYou already are inside an arena."));
            return;
        }

        Arena arena = this.getArena(id);
        // Arena does not exists
        if (arena == null) {
            p.sendMessage(ChatUtils.colorMsg("&cThis arena does not exist."));
            return;
        }
        // Game running inside arena
        if (!arena.isEnabled()) {
            p.sendMessage(ChatUtils.colorMsg("&cThis arena is currenlty disabled."));
            return;
        }
        // Arena is full
        if (arena.isFull()) {
            p.sendMessage(ChatUtils.colorMsg("&cThis arena is full."));
            return;
        }

        // Adds the player to the arena player list
        arena.addPlayer(p);
    }

    public boolean addPlayerToArena(Player p) {
        if (this.isInArena(p)) {
            p.sendMessage(ChatUtils.colorMsg("&cYou already are inside an arena."));
            return false;
        }

        Arena arena = getFirstArena();
        // Arena does not exists
        if (arena == null) {
            p.sendMessage(ChatUtils.colorMsg("&cNo arena found."));
            return false;
        }
        // Game running inside arena
        if (!arena.isEnabled()) {
            p.sendMessage(ChatUtils.colorMsg("&cThis arena is currently disabled."));
            return false;
        }
        // Arena is full
        if (arena.isFull()) {
            p.sendMessage(ChatUtils.colorMsg("&cThis arena is full."));
            return false;
        }

        // Adds the player to the arena player list
        arena.addPlayer(p);
        return true;
    }


    /**
     * Removes the player from a certain arena.
     *
     * @param p the player to remove from the arena
     */
    public boolean removePlayer(Player p, Arena arena) {
        // Check arena validity
        if (arena == null) {
            return false;
        }

        // Remove player from arena
        arena.removePlayer(p);

        return true;
    }

    public boolean removePlayer(ArenaPlayer arenaPlayer) {
        Arena arena = getArena(arenaPlayer.getArenaId());

        return removePlayer(arenaPlayer.getPlayer(), arena);
    }

    public boolean deleteArena(int id) {
        Optional<Arena> optionalArena = arenas.stream().filter(ar -> ar.getId() == id).findFirst();
        if (!optionalArena.isPresent()) {
            return false;
        }
        Arena arena = optionalArena.get();

        // Disable kick players and disable arena
        arena.kickAll();
        arena.disable();

        // Remove from cache
        arenas.removeIf(ar -> ar.getId() == id);

        // Remove from config
        plugin.getConfig().set("arenas."+id, null);
        plugin.saveConfig();

        return true;
    }

    public void loadArenas() {
        if (plugin.getConfig().getConfigurationSection("arenas") == null) {
            ConsoleUtils.coloredLog("WARNING: There is no arena configured yet, make sure to register at least one.", ChatColor.YELLOW);
            return;
        }
        plugin.getConfig().getConfigurationSection("arenas").getKeys(false).forEach(section -> {
            try {
                World world = Bukkit.getWorld(plugin.getConfig().getString("arenas."+section+".world"));
                int maxPlayers = plugin.getConfig().getInt("arenas."+section+".max-players");
                Location spawnLocation = new Location(
                        world,
                        plugin.getConfig().getInt("arenas."+section+".spawn.x"),
                        plugin.getConfig().getInt("arenas."+section+".spawn.y"),
                        plugin.getConfig().getInt("arenas."+section+".spawn.z"));
                Location lobbyTop = new Location(
                        world,
                        plugin.getConfig().getInt("arenas."+section+".lobby.top.x"),
                        plugin.getConfig().getInt("arenas."+section+".lobby.top.y"),
                        plugin.getConfig().getInt("arenas."+section+".lobby.top.z"));
                Location lobbyBottom = new Location(
                        world,
                        plugin.getConfig().getInt("arenas."+section+".lobby.bottom.x"),
                        plugin.getConfig().getInt("arenas."+section+".lobby.bottom.y"),
                        plugin.getConfig().getInt("arenas."+section+".lobby.bottom.z"));

                Arena arena = new Arena(Integer.parseInt(section), maxPlayers, spawnLocation, lobbyTop, lobbyBottom);

                arenas.add(arena);
                ConsoleUtils.coloredLog("Loaded arena with id: "+section, ChatColor.GREEN);
            } catch (Exception ex) {
                ex.printStackTrace();
                ConsoleUtils.coloredLog("Failed to load arena with id: "+section, ChatColor.RED);
            }

        });

    }

}