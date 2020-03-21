package de.pauhull.npcapi.impl;

import com.mojang.authlib.GameProfile;
import org.bukkit.Location;

/**
 * Created by Paul
 * on 24.02.2019
 *
 * @author pauhull
 */
public interface NmsHelper {

    SimplePacket[] getInfoAddAndRemovePacket(GameProfile profile, int i, String gameMode);

    SimplePacket getSpawnPacket(int entityId, GameProfile profile, Location location);

    SimplePacket getRotationPacket(int entityId, float yaw);

    SimplePacket getDestroyPacket(int entityId);

}
