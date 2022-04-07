package com.techwolfx.knockffa.arena;

import com.techwolfx.knockffa.data.ArenaPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

public class ScoreboardArena {

    private static final ScoreboardManager manager = Bukkit.getScoreboardManager();
    private final Arena arena;

    // Prevent instantiation
    public ScoreboardArena(Arena arena) {
        this.arena = arena;
    }

    private Scoreboard buildScoreboard(ArenaPlayer p) {
        /*
        Scoreboard scoreboard = manager.getNewScoreboard();
        String SCOREBOARD_NAME = "play" + arena.getId();
        String HIDE_TEAM = "hide" + arena.getId();

        if (scoreboard.getObjective(SCOREBOARD_NAME) != null) {
            Objects.requireNonNull(scoreboard.getObjective(SCOREBOARD_NAME)).unregister();
        }
        if (scoreboard.getTeam(HIDE_TEAM) != null) {
            Objects.requireNonNull(scoreboard.getTeam(HIDE_TEAM)).unregister();
        }

        Objective objective = scoreboard.registerNewObjective(SCOREBOARD_NAME, "dummy", ChatUtils.colorMsg("&a&lKnockFFA"));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        String roleStr = p.getPlayer().getName();

        List<String> linesReversed = Lists.reverse(Arrays.asList(
                "",
                "&ePlayers ",
                "&e➥ &7"+arena.getPlayers().size() + "/" + arena.getMaxPlayers(),
                " ",
                "&eKills",
                "&e➥ &7"+roleStr,
                "  ",
                "&8Beta 1.0"
        ));

        for (int i = 0; i < linesReversed.size(); i++) {
            objective.getScore(ChatUtils.colorMsg(linesReversed.get(i))).setScore(i);
        }*/
        ScoreboardWrapper sc_wrapper = new ScoreboardWrapper("Custom Title");
        sc_wrapper.addLine("&cPlayer name");
        sc_wrapper.addLine(p.getPlayer().getName());
        sc_wrapper.addBlankSpace();
        sc_wrapper.addLine("Alpha 0.1");

        return sc_wrapper.getScoreboard();
    }

    public void reloadScoreboard() {
        for (ArenaPlayer arenaPlayer : arena.getPlayers()) {
            arenaPlayer.getPlayer().setScoreboard(buildScoreboard(arenaPlayer));
        }
    }

    public void addPlayer(ArenaPlayer p) {
        p.getPlayer().setScoreboard(buildScoreboard(p));
        reloadScoreboard();
    }

    public void removePlayer(Player p) {
        assert manager != null;
        p.setScoreboard(manager.getNewScoreboard());
        reloadScoreboard();
    }

}
