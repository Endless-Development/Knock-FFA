package com.techwolfx.knockffa.commands.subcommands;

import com.techwolfx.knockffa.arena.Arena;
import com.techwolfx.knockffa.arena.ArenaManager;
import com.techwolfx.knockffa.commands.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ArenaListCMD extends SubCommand {
    @Override
    public String getName() {
        return "list";
    }

    @Override
    public String getDesc() {
        return "Show all the registered arenas.";
    }

    @Override
    public String getSyntax() {
        return "/kffa list";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!sender.hasPermission("kffa.list")) {
            noPermission(sender);
            return;
        }

        sender.sendMessage(ChatColor.GREEN + "Registered arenas:");
        for (Arena arena : ArenaManager.getManager().getArenas()) {
            sender.sendMessage(ChatColor.YELLOW + arena.toString());
        }
    }
}
