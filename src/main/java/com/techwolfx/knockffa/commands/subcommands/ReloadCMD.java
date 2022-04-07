package com.techwolfx.knockffa.commands.subcommands;

import com.techwolfx.knockffa.Knockffa;
import com.techwolfx.knockffa.commands.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ReloadCMD extends SubCommand {
    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getDesc() {
        return "Reloads the plugin files";
    }

    @Override
    public String getSyntax() {
        return "/kffa reload";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!sender.hasPermission("kffa.reload")) {
            noPermission(sender);
            return;
        }
        Knockffa.getInstance().reloadConfig();
        Knockffa.getInstance().getConfigManager().reload();
        sender.sendMessage(ChatColor.GREEN + "Configuration files reloaded.");
    }
}
