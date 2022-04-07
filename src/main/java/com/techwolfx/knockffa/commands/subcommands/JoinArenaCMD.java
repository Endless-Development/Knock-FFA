package com.techwolfx.knockffa.commands.subcommands;

import com.techwolfx.knockffa.commands.SubCommand;
import com.techwolfx.knockffa.arena.ArenaManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class JoinArenaCMD extends SubCommand {
    @Override
    public String getName() {
        return "join";
    }

    @Override
    public String getDesc() {
        return "Join an existing arena.";
    }

    @Override
    public String getSyntax() {
        return "/kffa join [id]";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!sender.hasPermission("kffa.join")) {
            noPermission(sender);
            return;
        }

        if(!(sender instanceof Player)){
            sender.sendMessage(ChatColor.RED + "This command can be executed only by a player.");
            return;
        }
        Player p = (Player) sender;
        if(args.length > 2 || args.length < 1){
            warnInvalidArgs(p);
            return;
        }
        if (args.length == 2) {
            int id;
            try {
                id = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                p.sendMessage(ChatColor.RED + "Invalid arena ID");
                return;
            }
            ArenaManager.getManager().addPlayerToArena(p, id);
            return;
        }

        ArenaManager.getManager().addPlayerToArena(p);
    }
}
