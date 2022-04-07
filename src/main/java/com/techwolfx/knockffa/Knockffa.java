package com.techwolfx.knockffa;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.techwolfx.knockffa.arena.ArenaManager;
import com.techwolfx.knockffa.commands.CmdTabCompletion;
import com.techwolfx.knockffa.commands.CommandManager;
import com.techwolfx.knockffa.database.Database;
import com.techwolfx.knockffa.database.DatabaseCache;
import com.techwolfx.knockffa.database.SQLite;
import com.techwolfx.knockffa.events.PlayerEvents;
import com.techwolfx.knockffa.placeholders.Placeholders;
import com.techwolfx.knockffa.utils.ConsoleUtils;
import com.techwolfx.knockffa.utils.VersionUtils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class Knockffa extends JavaPlugin {

    @Getter
    private static Knockffa instance;
    @Getter
    private Database db;
    @Getter
    private int version;
    @Getter
    private static WorldEditPlugin worldEdit;
    @Getter
    private ConfigManager configManager;
    @Getter
    private DatabaseCache dbCache;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        this.configManager = new ConfigManager(this);
        this.version = VersionUtils.getVersionNumber();

        this.db = new SQLite(this);
        this.dbCache = new DatabaseCache(this);

        if (Bukkit.getPluginManager().getPlugin("WorldEdit") != null) {
            worldEdit = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
            ConsoleUtils.coloredLog("Hooked WorldEdit.", ChatColor.GREEN);
        } else {
            ConsoleUtils.coloredLog("Fatal error: Could not find WorldEdit.", ChatColor.RED);
            getPluginLoader().disablePlugin(this);
        }

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            // Register placeholder expansion
            new Placeholders(this).register();
            ConsoleUtils.coloredLog("Hooked PlaceholderAPI.", ChatColor.GREEN);
        } else {
            ConsoleUtils.coloredLog("Warning: Could not find PlaceholderAPI.", ChatColor.RED);
        }

        getServer().getPluginManager().registerEvents(new PlayerEvents(), this);

        Objects.requireNonNull(getCommand("kffa")).setExecutor(new CommandManager());
        Objects.requireNonNull(getCommand("kffa")).setTabCompleter(new CmdTabCompletion());

        ArenaManager.getManager().loadArenas();
    }

    @Override
    public void onDisable() {
        dbCache.save();
    }

}
