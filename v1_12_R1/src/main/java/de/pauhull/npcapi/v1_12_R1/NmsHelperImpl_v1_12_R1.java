package de.pauhull.npcapi.v1_12_R1;

import com.mojang.authlib.GameProfile;
import de.pauhull.npcapi.impl.NmsHelper;
import de.pauhull.npcapi.impl.SimplePacket;
import net.minecraft.server.v1_12_R1.*;
import net.minecraft.server.v1_12_R1.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.minecraft.server.v1_12_R1.PacketPlayOutPlayerInfo.PlayerInfoData;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.util.CraftChatMessage;

import java.util.ArrayList;
import java.util.List;

import static de.pauhull.npcapi.util.ReflectionUtils.getValue;
import static de.pauhull.npcapi.util.ReflectionUtils.setValue;

/**
 * Created by Paul
 * on 24.02.2019
 *
 * @author pauhull
 */
public class NmsHelperImpl_v1_12_R1 implements NmsHelper {

    @Override
    public SimplePacket[] getInfoAddAndRemovePacket(GameProfile profile, int i, String gameMode) {
        PacketPlayOutPlayerInfo infoAddPacket = new PacketPlayOutPlayerInfo();
        PlayerInfoData data = infoAddPacket.new PlayerInfoData(profile, i, EnumGamemode.valueOf(gameMode), CraftChatMessage.fromString(profile.getName())[0]);
        List<PlayerInfoData> playersToAdd = (List<PlayerInfoData>) getValue(infoAddPacket, "b");
        playersToAdd.add(data);
        setValue(infoAddPacket, "a", EnumPlayerInfoAction.ADD_PLAYER);
        setValue(infoAddPacket, "b", playersToAdd);

        PacketPlayOutPlayerInfo infoRemovePacket = new PacketPlayOutPlayerInfo();
        List<PlayerInfoData> playersToRemove = (List<PlayerInfoData>) getValue(infoRemovePacket, "b");
        playersToRemove.add(data);
        setValue(infoRemovePacket, "a", EnumPlayerInfoAction.REMOVE_PLAYER);
        setValue(infoRemovePacket, "b", playersToRemove);

        return new SimplePacket[]{
                new SimplePacketImpl_v1_12_R1(infoAddPacket),
                new SimplePacketImpl_v1_12_R1(infoRemovePacket)
        };
    }

    @Override
    public SimplePacket getSpawnPacket(int entityId, GameProfile profile, Location location) {
        byte yaw = (byte) ((int) (location.getYaw() * 256.0F / 360.0F));
        byte pitch = (byte) ((int) (location.getPitch() * 256.0F / 360.0F));

        PacketPlayOutNamedEntitySpawn spawnPacket = new PacketPlayOutNamedEntitySpawn();
        setValue(spawnPacket, "a", entityId);
        setValue(spawnPacket, "b", profile.getId());
        setValue(spawnPacket, "c", location.getX());
        setValue(spawnPacket, "d", location.getY());
        setValue(spawnPacket, "e", location.getZ());
        setValue(spawnPacket, "f", yaw);
        setValue(spawnPacket, "g", pitch);
        setValue(spawnPacket, "h", new DataWatcher(null));
        setValue(spawnPacket, "i", new ArrayList());

        return new SimplePacketImpl_v1_12_R1(spawnPacket);
    }

    @Override
    public SimplePacket getRotationPacket(int entityId, float yaw) {
        PacketPlayOutEntityHeadRotation rotationPacket = new PacketPlayOutEntityHeadRotation();
        setValue(rotationPacket, "a", entityId);
        setValue(rotationPacket, "b", (byte) ((int) (yaw * 256.0F / 360.0F)));
        return new SimplePacketImpl_v1_12_R1(rotationPacket);
    }

    @Override
    public SimplePacket getDestroyPacket(int entityId) {
        return new SimplePacketImpl_v1_12_R1(new PacketPlayOutEntityDestroy(entityId));
    }

}
