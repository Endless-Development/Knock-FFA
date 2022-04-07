package com.techwolfx.knockffa.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.logging.Level;

public class ConsoleUtils {

    private static final String prefix = "[KnockFFA] ";

    public static void coloredLog(String s, ChatColor color) {
        Bukkit.getConsoleSender().sendMessage(color+prefix+s);
    }

    public static void logInfo(String s){
        Bukkit.getLogger().log(Level.INFO, prefix+s);
    }
    public static void logWarning(String s){
        Bukkit.getLogger().log(Level.WARNING, prefix+s);
    }
    public static void logSevere(String s){
        Bukkit.getLogger().log(Level.SEVERE, prefix+s);
    }

    public static void loadingError(String info, String log){
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + prefix + "Failed to load " + info + ": " + log);
    }

}
