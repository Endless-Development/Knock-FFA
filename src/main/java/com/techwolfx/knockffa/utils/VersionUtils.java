package com.techwolfx.knockffa.utils;

import org.bukkit.Bukkit;

public class VersionUtils {
    public static int getVersionNumber(){
        String fullVersion = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        return Integer.parseInt(fullVersion.split("_")[1].split("_R")[0].replace("v", ""));
    }
}
