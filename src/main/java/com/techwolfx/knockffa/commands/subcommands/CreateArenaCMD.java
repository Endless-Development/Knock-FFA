package com.techwolfx.knockffa.commands.subcommands;

import com.techwolfx.knockffa.commands.SubCommand;
import com.techwolfx.knockffa.arena.ArenaManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CreateArenaCMD extends SubCommand {
    @Override
    public String getName() {
        return "create";
    }

    @Override
    public String getDesc() {
        return "Create an arena.";
    }

    @Override
    public String getSyntax() {
        return "/kffa create [maxPlayers]";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!sender.hasPermission("kffa.create")) {
            noPermission(sender);
            return;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can be executed only by a player.");
            return;
        }
        Player p = (Player) sender;
        if (args.length < 1 || args.length > 2) {
            warnInvalidArgs(p);
            return;
        }

        int maxPlayers = 50;
        if (args.length == 2) {
            try {
                maxPlayers = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                p.sendMessage(ChatColor.RED + "Invalid max players value.");
                return;
            }
        }

        ArenaManager.getManager().createArena(p, maxPlayers);
    }
}
