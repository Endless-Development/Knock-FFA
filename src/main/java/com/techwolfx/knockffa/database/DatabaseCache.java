package com.techwolfx.knockffa.database;

import com.techwolfx.knockffa.Knockffa;
import com.techwolfx.knockffa.utils.ConsoleUtils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.HashMap;


public class DatabaseCache {

    private final Knockffa plugin;
    private final BukkitScheduler scheduler = Bukkit.getScheduler();

    @Getter
    private final HashMap<String, User> users = new HashMap<>();
    private Integer task = null;
    private final long interval;

    public DatabaseCache(Knockffa plugin) {
        this.plugin = plugin;
        this.interval = plugin.getConfigManager().getDbUpdateInterval() * 60 * 20L;
        startTask();
    }


    /**
     * Get a User object by his Player Object from cache,
     * if not found, fetch data from database and store result in cache.
     *
     * @param player the player object
     * @return the User object corresponding to the uuid
     */
    public User getUserOrCreate(Player player) {
        User user;
        String uuid = player.getUniqueId().toString();

        if (users.containsKey(uuid)) {
            user = users.get(uuid);
        } else {
            user = plugin.getDb().getUser(uuid);
            if (user == null) {
                user = plugin.getDb().registerUser(player);
            }
            users.put(uuid, user);
        }

        return user;
    }

    /**
     * Get a User object by his UUID from cache,
     * if not found, fetch data from database and store result in cache.
     *
     * NOTE: This method is less safe than the other implementation of it:
     * if the user is not registered in database this method will {@return null}.
     *
     * @param uuid the player object
     * @return the User object corresponding to the uuid
     */
    public User getUser(String uuid) {
        User user;

        if (users.containsKey(uuid)) {
            user = users.get(uuid);
        } else {
            user = plugin.getDb().getUser(uuid);
            if (user == null) {
                return null;
            }
            users.put(uuid, user);
        }

        return user;
    }

    /**
     * Update the deaths of a certain user.
     *
     * @param player the player object
     * @param deaths the kills to add to the player
     */
    public void addDeaths(Player player, int deaths) {
        User user = getUserOrCreate(player);
        user.addDeaths(deaths);
        user.setStreak(0);

        users.put(player.getUniqueId().toString(), user);
    }

    /**
     * Update the kills of a certain user.
     *
     * @param player the player object
     * @param kills the kills to add to the player
     */
    public void addKills(Player player, int kills) {
        User user = getUserOrCreate(player);
        user.addKills(kills); // updates also streak

        users.put(player.getUniqueId().toString(), user);
    }

    public void save() {
        ConsoleUtils.coloredLog("Storing cached data to database...", ChatColor.YELLOW);
        users.forEach((s, user) -> {
            plugin.getDb().updateUser(user);
        });
        ConsoleUtils.coloredLog("Stored " + users.size() + " users in database.", ChatColor.GREEN);
        users.clear();
    }

    private void startTask() {
        // If task is null, or it is not running, start it
        if (task == null || !scheduler.isCurrentlyRunning(task)) {
            ConsoleUtils.coloredLog("Started database cache system.", ChatColor.GREEN);
            task = scheduler.scheduleSyncRepeatingTask(plugin, this::save, interval, interval);
        }
    }

}
