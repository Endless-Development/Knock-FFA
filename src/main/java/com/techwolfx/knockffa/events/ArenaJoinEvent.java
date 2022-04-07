package com.techwolfx.knockffa.events;

import com.techwolfx.knockffa.arena.Arena;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ArenaJoinEvent extends Event {

    private static final HandlerList HANDLERS_LIST = new HandlerList();
    @Getter
    private final Arena arena;
    @Getter
    private final Player player;

    public ArenaJoinEvent(Arena arena, Player player) {
        this.arena = arena;
        this.player = player;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

}
