package de.pauhull.npcapi.event;

import de.pauhull.npcapi.npc.Npc;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * Created by Paul
 * on 23.02.2019
 *
 * @author pauhull
 */
public class PlayerClickNpcEvent extends PlayerEvent {

    private static final HandlerList handlerList = new HandlerList();
    private Npc npc;
    private Action action;

    public PlayerClickNpcEvent(Player player, Npc npc, Action action) {
        super(player);
        this.npc = npc;
        this.action = action;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public Npc getNpc() {
        return npc;
    }

    public Action getAction() {
        return action;
    }

    public enum Action {

        INTERACT,
        ATTACK,
        INTERACT_AT

    }

}
