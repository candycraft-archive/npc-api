package de.pauhull.npcapi.packet;

import de.pauhull.npcapi.NpcApi;
import de.pauhull.npcapi.event.PlayerClickNpcEvent;
import de.pauhull.npcapi.event.PlayerClickNpcEvent.Action;
import de.pauhull.npcapi.npc.Npc;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.inventivetalent.packetlistener.handler.PacketHandler;
import org.inventivetalent.packetlistener.handler.ReceivedPacket;
import org.inventivetalent.packetlistener.handler.SentPacket;

import static de.pauhull.npcapi.util.ReflectionUtils.getValue;

/**
 * Created by Paul
 * on 05.01.2019
 *
 * @author pauhull
 */
public class PacketReader extends PacketHandler {

    @Override
    public void onSend(SentPacket sentPacket) {

    }

    @Override
    public void onReceive(ReceivedPacket receivedPacket) {
        if (receivedPacket == null)
            return;

        Player player = receivedPacket.getPlayer();
        if (receivedPacket.getPacket().getClass().getSimpleName().equals("PacketPlayInUseEntity")) {

            int id = (int) getValue(receivedPacket.getPacket(), "a");
            Object actionObj = getValue(receivedPacket.getPacket(), "action");

            Npc npc = Npc.getById(id);
            if (player != null && npc != null) {
                Bukkit.getScheduler().runTask(NpcApi.getInstance(), () -> {
                    Action action = null;

                    if (actionObj != null) {
                        action = Action.valueOf(actionObj.toString());
                    }

                    PlayerClickNpcEvent event = new PlayerClickNpcEvent(player, npc, action);
                    Bukkit.getPluginManager().callEvent(event);
                });
            }
        }
    }

}
