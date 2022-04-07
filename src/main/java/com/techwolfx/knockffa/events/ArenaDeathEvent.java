package com.techwolfx.knockffa.events;

import com.techwolfx.knockffa.arena.Arena;
import com.techwolfx.knockffa.data.ArenaPlayer;
import com.techwolfx.knockffa.enums.DeathCause;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ArenaDeathEvent extends Event {

    private static final HandlerList HANDLERS_LIST = new HandlerList();
    @Getter
    private final Arena arena;
    @Getter
    private final ArenaPlayer arenaPlayer;
    @Getter
    private final DeathCause deathCause;
    @Getter
    private final ArenaPlayer killer;

    public ArenaDeathEvent(Arena arena, ArenaPlayer arenaPlayer, DeathCause deathCause, ArenaPlayer killer) {
        this.arena = arena;
        this.arenaPlayer = arenaPlayer;
        this.deathCause = deathCause;
        this.killer = killer;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

}
