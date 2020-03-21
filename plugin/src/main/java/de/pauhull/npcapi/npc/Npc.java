package de.pauhull.npcapi.npc;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import de.pauhull.npcapi.NpcApi;
import de.pauhull.npcapi.impl.NmsHelper;
import de.pauhull.npcapi.impl.SimplePacket;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Paul
 * on 04.01.2019
 *
 * @author pauhull
 */
public class Npc {

    private static List<Npc> npcs = new ArrayList<>();

    private int entityId;

    private boolean showInTablist;

    private Location location;

    private SkinData skin;

    private GameProfile profile;

    private SimplePacket infoAddPacket;
    private SimplePacket spawnPacket;
    private SimplePacket rotationPacket;
    private SimplePacket infoRemovePacket;
    private SimplePacket destroyPacket;

    public Npc(Location location, UUID uuid, String name, SkinData skin, boolean showInTablist) {
        if (name != null && name.length() > 16) {
            name = name.substring(0, 16);
        }

        this.entityId = findFreeEntityId();
        this.profile = new GameProfile(uuid, name);
        this.profile.getProperties().put("textures", new Property("textures", skin.getTexture(), skin.getSignature()));
        this.location = location;
        this.showInTablist = showInTablist;
        this.skin = skin;
        this.initPackets();
    }

    public static List<Npc> getNpcs() {
        return npcs;
    }

    public static Npc getById(int id) {
        for (Npc npc : npcs) {
            if (npc.entityId == id) {
                return npc;
            }
        }

        return null;
    }

    private static int findFreeEntityId() {
        List<Integer> currentEntityIds = new ArrayList<>();
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                currentEntityIds.add(entity.getEntityId());
            }
        }
        for (Npc npc : npcs) {
            currentEntityIds.add(npc.entityId);
        }

        for (int id = 2000; id < 10000; id++) {
            if (!currentEntityIds.contains(id)) {
                return id;
            }
        }

        return 0;
    }

    private void initPackets() {
        NmsHelper helper = NpcApi.getInstance().getNmsHelper();
        SimplePacket[] infoAddAndRemovePacket = helper.getInfoAddAndRemovePacket(profile, 1, "NOT_SET");
        infoAddPacket = infoAddAndRemovePacket[0];
        spawnPacket = helper.getSpawnPacket(entityId, profile, location);
        rotationPacket = helper.getRotationPacket(entityId, location.getYaw());
        infoRemovePacket = infoAddAndRemovePacket[1];
        destroyPacket = helper.getDestroyPacket(entityId);
    }

    public void spawn() {
        npcs.add(this);
        for (Player player : Bukkit.getOnlinePlayers()) {
            spawn(player);
        }
    }

    public void spawn(Player player) {
        if (!npcs.contains(this)) return;
        if (!location.getWorld().equals(player.getWorld())) return;
        // you first have to send the tablist information to all players or the npc will be invisible
        infoAddPacket.send(player);
        // then you spawn the npc with the spawnPacket
        spawnPacket.send(player);
        // you have to rotate the npc's head by yourself (which is really weird)
        rotationPacket.send(player);
        // half a second after the npc is spawned you can safely remove it from the tablist if you want
        if (!showInTablist) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(NpcApi.getInstance(), () -> {
                infoRemovePacket.send(player);
            }, 30L);
        }
    }

    public void respawn(Player player) {
        despawn(player);
        spawn(player);
    }

    public void despawn() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            // if the tablist information isnt removed already you have to do it here
            if (showInTablist) {
                infoRemovePacket.send(player);
            }
            // destroy the entity on the client side
            destroyPacket.send(player);
        }
    }

    public void despawn(Player player) {
        if (showInTablist) {
            infoRemovePacket.send(player);
        }
        destroyPacket.send(player);
    }

    public String getName() {
        return profile.getName();
    }

    public UUID getUuid() {
        return profile.getId();
    }

    public GameProfile getProfile() {
        return profile;
    }

    public SkinData getSkin() {
        return skin;
    }

    public Location getLocation() {
        return location;
    }

    public boolean isShowInTablist() {
        return showInTablist;
    }

    public int getEntityId() {
        return entityId;
    }

}
