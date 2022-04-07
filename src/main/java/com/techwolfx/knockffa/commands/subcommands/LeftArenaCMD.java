package com.techwolfx.knockffa.commands.subcommands;

import com.techwolfx.knockffa.commands.SubCommand;
import com.techwolfx.knockffa.arena.Arena;
import com.techwolfx.knockffa.arena.ArenaManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LeftArenaCMD extends SubCommand {
    @Override
    public String getName() {
        return "left";
    }

    @Override
    public String getDesc() {
        return "Left the arena in which you currently are inside.";
    }

    @Override
    public String getSyntax() {
        return "/kffa left";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!sender.hasPermission("kffa.left")) {
            noPermission(sender);
            return;
        }

        if(!(sender instanceof Player)){
            sender.sendMessage(ChatColor.RED + "This command can be executed only by a player.");
            return;
        }
        Player p = (Player) sender;
        if(args.length != 1){
            warnInvalidArgs(p);
            return;
        }
        if (ArenaManager.getManager().isInArena(p)) {
            Arena arena = ArenaManager.getManager().getPlayerArena(p);
            ArenaManager.getManager().removePlayer(p, arena);
            p.sendMessage(ChatColor.GREEN + "You left the arena.");
        } else {
            p.sendMessage(ChatColor.RED + "You are not currently inside an arena.");
        }
    }
}
