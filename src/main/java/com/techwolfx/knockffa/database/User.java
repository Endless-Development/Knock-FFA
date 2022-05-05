package com.techwolfx.knockffa.database;

import lombok.Getter;
import lombok.Setter;

import java.math.RoundingMode;
import java.text.DecimalFormat;
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
    @Getter @Setter
    private int highStreak;

    public double getKdr() {
        if (deaths < 2)
            return kills;
        DecimalFormat df = new DecimalFormat("0.00");
        double kdr = (double) kills / deaths;
        return Double.parseDouble(df.format(kdr));
    }

    public void addKills(int kills) {
        this.kills += kills;
        this.streak += kills;
        if (this.streak > this.highStreak) {
            this.highStreak = this.streak;
        }
    }

    public void addDeaths(int deaths) {
        this.deaths += deaths;
        if (this.streak > this.highStreak) {
            this.highStreak = this.streak;
        }
        this.streak = 0;
    }

    public User(String uuid, String username, int kills, int deaths, int streak, int highStreak) {
        this.UUID       = uuid;
        this.username   = username;
        this.kills      = kills;
        this.deaths     = deaths;
        this.streak     = streak;
        this.highStreak = highStreak;
    }
}
