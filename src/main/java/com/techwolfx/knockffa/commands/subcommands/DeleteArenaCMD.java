package com.techwolfx.knockffa.commands.subcommands;

import com.techwolfx.knockffa.arena.ArenaManager;
import com.techwolfx.knockffa.commands.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class DeleteArenaCMD extends SubCommand {
    @Override
    public String getName() {
        return "delete";
    }

    @Override
    public String getDesc() {
        return "Deletes an arena.";
    }

    @Override
    public String getSyntax() {
        return "/kffa delete <id>";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!sender.hasPermission("kffa.delete")) {
            noPermission(sender);
            return;
        }

        if (args.length != 2) {
            warnInvalidArgs(sender);
            return;
        }

        int id;
        try {
            id = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Invalid arena ID");
            return;
        }

        boolean res = ArenaManager.getManager().deleteArena(id);

        if (res) {
            sender.sendMessage(ChatColor.GREEN + "Arena with id "+id+" deleted successfully.");
        } else {
            sender.sendMessage(ChatColor.RED + "Arena with id "+id+" not found.");
        }
    }
}
