package com.techwolfx.knockffa.data;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

public class ArenaPlayer {

    @Getter
    private final Player player;
    @Getter
    private final int arenaId;
    @Getter @Setter
    private boolean spectator = false;

    public ArenaPlayer(Player player, int arenaId) {
        this.player = player;
        this.arenaId = arenaId;
    }

}
