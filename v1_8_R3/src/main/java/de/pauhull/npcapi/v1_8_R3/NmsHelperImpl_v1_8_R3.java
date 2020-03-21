package de.pauhull.npcapi.v1_8_R3;

import com.mojang.authlib.GameProfile;
import de.pauhull.npcapi.impl.NmsHelper;
import de.pauhull.npcapi.impl.SimplePacket;
import net.minecraft.server.v1_8_R3.*;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo.PlayerInfoData;
import net.minecraft.server.v1_8_R3.WorldSettings.EnumGamemode;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftChatMessage;

import java.util.List;

import static de.pauhull.npcapi.util.ReflectionUtils.getValue;
import static de.pauhull.npcapi.util.ReflectionUtils.setValue;

/**
 * Created by Paul
 * on 24.02.2019
 *
 * @author pauhull
 */
public class NmsHelperImpl_v1_8_R3 implements NmsHelper {

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
                new SimplePacketImpl_v1_8_R3(infoAddPacket),
                new SimplePacketImpl_v1_8_R3(infoRemovePacket)
        };
    }

    @Override
    public SimplePacket getSpawnPacket(int entityId, GameProfile profile, Location location) {
        byte yaw = (byte) ((int) (location.getYaw() * 256.0F / 360.0F));
        byte pitch = (byte) ((int) (location.getPitch() * 256.0F / 360.0F));

        PacketPlayOutNamedEntitySpawn spawnPacket = new PacketPlayOutNamedEntitySpawn();
        setValue(spawnPacket, "a", entityId);
        setValue(spawnPacket, "b", profile.getId());
        setValue(spawnPacket, "c", MathHelper.floor(location.getX() * 32.0D));
        setValue(spawnPacket, "d", MathHelper.floor(location.getY() * 32.0D));
        setValue(spawnPacket, "e", MathHelper.floor(location.getZ() * 32.0D));
        setValue(spawnPacket, "f", yaw);
        setValue(spawnPacket, "g", pitch);
        setValue(spawnPacket, "h", 0);
        DataWatcher watcher = new DataWatcher(null);
        watcher.a(6, (float) 20);
        watcher.a(10, (byte) 127);
        setValue(spawnPacket, "i", watcher);

        return new SimplePacketImpl_v1_8_R3(spawnPacket);
    }

    @Override
    public SimplePacket getRotationPacket(int entityId, float yaw) {
        PacketPlayOutEntityHeadRotation rotationPacket = new PacketPlayOutEntityHeadRotation();
        setValue(rotationPacket, "a", entityId);
        setValue(rotationPacket, "b", (byte) ((int) (yaw * 256.0F / 360.0F)));
        return new SimplePacketImpl_v1_8_R3(rotationPacket);
    }

    @Override
    public SimplePacket getDestroyPacket(int entityId) {
        return new SimplePacketImpl_v1_8_R3(new PacketPlayOutEntityDestroy(entityId));
    }

}
