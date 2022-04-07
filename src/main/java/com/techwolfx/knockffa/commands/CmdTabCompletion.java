package com.techwolfx.knockffa.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class CmdTabCompletion implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if(args.length == 1){
            CommandManager cm = new CommandManager();
            List<String> subCmds = new ArrayList<>();
            for (SubCommand cmd : cm.getSubCommands()){
                subCmds.add(cmd.getName());
            }
            return subCmds;
        }
        return null;
    }
}
