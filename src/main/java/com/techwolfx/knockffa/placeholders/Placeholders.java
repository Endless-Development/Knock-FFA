package com.techwolfx.knockffa.placeholders;

import com.techwolfx.knockffa.Knockffa;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class Placeholders extends PlaceholderExpansion {

    private final Knockffa plugin;

    public Placeholders(Knockffa plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "techwolfx";
    }

    @Override
    public @NotNull String getAuthor() {
        return "MasterWolfx";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister(){
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {

        if(params.equalsIgnoreCase("kffa_kills")) {
            return Integer.toString(plugin.getDbCache().getUser(player.getUniqueId().toString()).getKills());
        }
        if(params.equalsIgnoreCase("kffa_deaths")) {
            return Integer.toString(plugin.getDbCache().getUser(player.getUniqueId().toString()).getDeaths());
        }
        if(params.equalsIgnoreCase("kffa_streak")) {
            return Integer.toString(plugin.getDbCache().getUser(player.getUniqueId().toString()).getStreak());
        }

        return null;
    }
}
