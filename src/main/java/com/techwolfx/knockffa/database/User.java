package com.techwolfx.knockffa.database;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

public class User {
    @Getter
    private final String UUID;
    @Getter
    private final String username;
    @Getter @Setter
    private int kills;
    @Getter @Setter
    private int deaths;
    @Getter @Setter
    private int streak;

    public void addKills(int kills) {
        this.kills += kills;
        this.streak += kills;
    }

    public void addDeaths(int deaths) {
        this.deaths += deaths;
    }

    public User(String uuid, String username, int kills, int deaths, int streak) {
        this.UUID       = uuid;
        this.username   = username;
        this.kills      = kills;
        this.deaths     = deaths;
        this.streak     = streak;
    }
}
