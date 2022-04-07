package com.techwolfx.knockffa.commands;

import com.techwolfx.knockffa.commands.subcommands.*;
import com.techwolfx.knockffa.utils.ChatUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

public class CommandManager implements CommandExecutor {

    private final ArrayList<SubCommand> subCommands = new ArrayList<>();
    public ArrayList<SubCommand> getSubCommands() {
        return subCommands;
    }

    // Constructor
    public CommandManager(){
        subCommands.add(new JoinArenaCMD());
        subCommands.add(new LeftArenaCMD());
        subCommands.add(new ArenaListCMD());
        subCommands.add(new CreateArenaCMD());
        subCommands.add(new DeleteArenaCMD());
        subCommands.add(new SetLobbyCMD());
        subCommands.add(new ReloadCMD());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("kffa.help")) {
            return false;
        }
        // If the command does not comprise a subcommand or if the subcommand is help
        if(args.length == 0 || args[0].equalsIgnoreCase("help")){
            sender.sendMessage(ChatUtils.colorMsg("&8&m---------------&c&o KnockFFA &8&m---------------"));
            for(SubCommand cmd : subCommands){
                ChatUtils.sendHoverableSuggestionMessage(sender, "&c"+cmd.getSyntax()+ " &8| &7" + cmd.getDesc(), "&7&oClick to copy.", cmd.getSyntax());
            }
            return false;
        }

        // Checking if the subcommand exists
        for (SubCommand subCommand : subCommands) {
            if (args[0].equalsIgnoreCase(subCommand.getName())) {
                subCommand.perform(sender, args);
                return false;
            }
        }
        sender.sendMessage(ChatColor.RED + "Command not found. View all the commands typing: /kffa help");
        return false;
    }
}
