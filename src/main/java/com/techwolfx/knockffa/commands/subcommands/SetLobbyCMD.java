package com.techwolfx.knockffa.commands.subcommands;

import com.sk89q.worldedit.bukkit.selections.Selection;
import com.techwolfx.knockffa.Knockffa;
import com.techwolfx.knockffa.arena.Arena;
import com.techwolfx.knockffa.arena.ArenaManager;
import com.techwolfx.knockffa.commands.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetLobbyCMD extends SubCommand {
    @Override
    public String getName() {
        return "setlobby";
    }

    @Override
    public String getDesc() {
        return "Set the lobby area and spawn for an arena.";
    }

    @Override
    public String getSyntax() {
        return "/kffa setlobby <arenaId>";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!sender.hasPermission("kffa.setlobby")) {
            noPermission(sender);
            return;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can be executed only by a player.");
            return;
        }
        Player p = (Player) sender;

        if (args.length != 2) {
            warnInvalidArgs(p);
            return;
        }

        Selection selection = Knockffa.getWorldEdit().getSelection(p);
        if (selection == null) {
            p.sendMessage(ChatColor.RED + "Please make a region selection first.");
            return;
        }

        int id;
        try {
            id = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            p.sendMessage(ChatColor.RED + "Invalid arena ID");
            return;
        }

        Arena arena = ArenaManager.getManager().getArena(id);
        if (arena == null) {
            p.sendMessage(ChatColor.RED + "Arena not found.");
            return;
        }

        arena.setLobby(p.getLocation(), selection);
        p.sendMessage(ChatColor.GREEN + "Lobby created for arena "+id+".");
    }
}
