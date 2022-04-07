package com.techwolfx.knockffa.utils;

import com.techwolfx.knockffa.Knockffa;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChatUtils {

    private static final Knockffa plugin = Knockffa.getInstance();

    public static void sendHoverableMessage(CommandSender sender, String msg, String hoverMsg){
        if(plugin.getVersion() > 8){
            TextComponent message = new TextComponent(ChatColor.translateAlternateColorCodes('&', msg));
            message.setHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', hoverMsg)).create() ) );
            sender.spigot().sendMessage(message);
        } else {
            if(sender instanceof Player){
                Player p = (Player) sender;
                TextComponent tc = new TextComponent();
                tc.setText( ChatColor.translateAlternateColorCodes('&', msg) );
                tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder( ChatColor.translateAlternateColorCodes('&', hoverMsg) ).create()));
                p.spigot().sendMessage(tc);
            } else{
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
            }
        }
    }

    public static void sendHoverableLinkMessage(CommandSender sender, String msg, String hoverMsg, String link){
        if(plugin.getVersion() > 8){
            TextComponent message = new TextComponent( ChatColor.translateAlternateColorCodes('&', msg) );
            message.setClickEvent( new ClickEvent( ClickEvent.Action.OPEN_URL, link ) );
            message.setHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', hoverMsg)).create() ) );
            sender.spigot().sendMessage(message);
        } else {
            if(sender instanceof Player){
                Player p = (Player) sender;
                TextComponent tc = new TextComponent();
                tc.setText(ChatColor.translateAlternateColorCodes('&', msg));
                tc.setHoverEvent( new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', hoverMsg)).create() ) );
                tc.setClickEvent( new ClickEvent(ClickEvent.Action.OPEN_URL, link ) );
                p.spigot().sendMessage(tc);
            } else {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
            }
        }
    }

    public static void sendHoverableSuggestionMessage(CommandSender sender, String msg, String hoverMsg, String suggestion){
        if(plugin.getVersion() > 8){
            TextComponent message = new TextComponent(ChatColor.translateAlternateColorCodes('&', msg));
            message.setClickEvent( new ClickEvent( ClickEvent.Action.SUGGEST_COMMAND, suggestion) );
            message.setHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', hoverMsg)).create() ) );
            sender.spigot().sendMessage(message);
        } else {
            if(sender instanceof Player){
                Player p = (Player) sender;
                TextComponent tc = new TextComponent();
                tc.setText(ChatColor.translateAlternateColorCodes('&', msg));
                tc.setHoverEvent( new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', hoverMsg)).create() ) );
                tc.setClickEvent( new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, suggestion));
                p.spigot().sendMessage(tc);
            } else {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
            }
        }
    }

    public static String colorMsg(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }

    public static String formatSeconds(long secondsCount){
        //Calculate the seconds:
        long seconds = secondsCount %60;
        secondsCount -= seconds;
        //Calculate the minutes:
        long minutesCount = secondsCount / 60;
        long minutes = minutesCount % 60;
        minutesCount -= minutes;
        //Calculate the hours:
        long hoursCount = minutesCount / 60;
        if(hoursCount == 0 && minutes == 0){
            return "" + seconds + "s";
        }
        if(hoursCount == 0){
            return "" + minutes + "m:" + seconds + "s";
        }
        return "" + hoursCount + "m:" + minutes + ":" + seconds + "s";
    }
}
