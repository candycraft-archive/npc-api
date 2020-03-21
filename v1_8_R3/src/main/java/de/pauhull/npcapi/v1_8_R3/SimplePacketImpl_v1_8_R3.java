package de.pauhull.npcapi.v1_8_R3;

import de.pauhull.npcapi.impl.SimplePacket;
import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 * Created by Paul
 * on 24.02.2019
 *
 * @author pauhull
 */
public class SimplePacketImpl_v1_8_R3 implements SimplePacket {

    private Packet packet;

    public SimplePacketImpl_v1_8_R3(Packet packet) {
        this.packet = packet;
    }

    @Override
    public void send(Player player) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    public Packet getPacket() {
        return packet;
    }

}
