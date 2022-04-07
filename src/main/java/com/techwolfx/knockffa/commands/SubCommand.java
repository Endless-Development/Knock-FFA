package com.techwolfx.knockffa.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public abstract class SubCommand {

    public abstract String getName();

    public abstract String getDesc();

    public abstract String getSyntax();

    public abstract void perform(CommandSender sender, String[] args);

    public void warnInvalidArgs(CommandSender sender){
        sender.sendMessage(ChatColor.RED + "Invalid arguments. Usage: %syntax%".replace("%syntax%", getSyntax()));
    }

    public void cantFindPlayer(CommandSender sender, String s){
        sender.sendMessage(ChatColor.RED + "Error: player called '"+s+"' not found.");
    }

    public void noPermission(CommandSender sender){
        sender.sendMessage(ChatColor.RED + "You don't have the permission to execute this command.");
    }
}
