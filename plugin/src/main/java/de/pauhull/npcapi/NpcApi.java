package de.pauhull.npcapi;

import de.pauhull.npcapi.impl.NmsHelper;
import de.pauhull.npcapi.listener.NpcListener;
import de.pauhull.npcapi.v1_12_R1.NmsHelperImpl_v1_12_R1;
import de.pauhull.npcapi.v1_8_R3.NmsHelperImpl_v1_8_R3;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Paul
 * on 23.02.2019
 *
 * @author pauhull
 */
public class NpcApi extends JavaPlugin {

    private static NpcApi instance;

    private NmsHelper nmsHelper;

    public static NpcApi getInstance() {
        return instance;
    }

    @Override
    public void onDisable() {
        instance = null;
    }

    @Override
    public void onEnable() {
        instance = this;

        new NpcListener(this);
        //PacketListenerAPI.addPacketHandler(new PacketReader());
    }

    public NmsHelper getNmsHelper() {
        if (nmsHelper != null) {
            return nmsHelper;
        }

        String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];

        if (version.equals("v1_12_R1")) {
            nmsHelper = new NmsHelperImpl_v1_12_R1();
        } else {
            nmsHelper = new NmsHelperImpl_v1_8_R3();
        }

        return nmsHelper;
    }

}
