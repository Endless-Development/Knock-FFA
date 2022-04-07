package com.techwolfx.knockffa.utils;

import com.techwolfx.knockffa.data.ArenaPlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import java.util.*;

public class Utils {

    public static void clearEffectFromPlayer(Player p ){
        for(PotionEffect potion : p.getActivePotionEffects()){
            p.removePotionEffect(potion.getType());
        }
    }

    public static void sendMsgToPlayerList(List<ArenaPlayer> list, String str){
        for(ArenaPlayer p : list){
            p.getPlayer().sendMessage(ChatUtils.colorMsg(str));
        }
    }

    public static void sendEffectsToPlayerList(List<ArenaPlayer> list, PotionEffectType[] potion){
        for(ArenaPlayer player : list){
            Player p = player.getPlayer();
            for (PotionEffectType potionEffectType : potion) {
                p.addPotionEffect(new PotionEffect(potionEffectType, 9999, 5));
            }
        }
    }

    public static void sendTitleToPlayerList(List<ArenaPlayer> list, String title, String subtitle){
        for(ArenaPlayer player : list){
            Player p = player.getPlayer();
            p.sendTitle(ChatUtils.colorMsg(title), ChatUtils.colorMsg(subtitle));
        }
    }

    public static void clearEffectFromPlayers(List<ArenaPlayer> list){
        for (ArenaPlayer p : list) {
            for (PotionEffect potion : p.getPlayer().getActivePotionEffects()) {
                p.getPlayer().removePotionEffect(potion.getType());
            }
        }
    }
    public static List<String> translateLore(String... lore) {
        // Set the lore of the item
        List<String> loreArr = new ArrayList<>();
        for (String line : lore) {
            loreArr.add(ChatUtils.colorMsg(line));
        }
        return loreArr;
    }

}
