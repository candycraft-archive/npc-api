package de.pauhull.npcapi.listener;

import de.pauhull.npcapi.NpcApi;
import de.pauhull.npcapi.npc.Npc;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Paul
 * on 03.01.2019
 *
 * @author pauhull
 */
public class NpcListener implements Listener {

    private NpcApi plugin;
    private Map<Player, List<Npc>> npcsInSight = new HashMap<>();

    public NpcListener(NpcApi plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        // when you join the world and the npcs get reloaded and a npc has a certain distance from you (48 blocks), the
        // skin won't be loaded so you have to respawn it when the player gets near it. after that the skin is loaded
        // forever, until the npc gets respawned (e.g. world switch)
        Player player = event.getPlayer();
        if (npcsInSight.containsKey(player)) {
            List<Npc> npcsInSight = this.npcsInSight.get(player);
            for (Npc npc : Npc.getNpcs()) {
                if (!npc.getLocation().getWorld().equals(player.getWorld())) {
                    continue;
                }

                if (!npcsInSight.contains(npc) && npc.getLocation().distanceSquared(player.getLocation()) <= 48 * 48) {
                    npc.respawn(player);
                    npcsInSight.add(npc);
                }
            }
            this.npcsInSight.put(player, npcsInSight);
        } else {
            this.npcsInSight.put(player, new ArrayList<>());
            this.onPlayerMove(event);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.npcsInSight.remove(event.getPlayer());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        for (Npc npc : Npc.getNpcs()) {
            npc.spawn(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (!event.getFrom().getWorld().equals(event.getTo().getWorld())) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                for (Npc npc : Npc.getNpcs()) {
                    npc.spawn(event.getPlayer());
                }
            }, 15);
            npcsInSight.remove(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            for (Npc npc : Npc.getNpcs()) {
                npc.spawn(event.getPlayer());
            }
        }, 5);
        npcsInSight.remove(event.getPlayer());
    }
}
