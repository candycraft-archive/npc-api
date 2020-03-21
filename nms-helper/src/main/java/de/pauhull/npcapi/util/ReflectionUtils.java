package de.pauhull.npcapi.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by Paul
 * on 04.01.2019
 *
 * @author pauhull
 */
public class ReflectionUtils {

    public static void setValue(Object obj, String fieldName, Object value) {
        try {
            Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(obj, value);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public static Object getValue(Object obj, String fieldName) {
        try {
            Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(obj);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void sendPacket(Object packet) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            sendPacket(player, packet);
        }
    }

    public static void sendPacket(Player player, Object packet) {
        try {
            Method getHandle = player.getClass().getMethod("getHandle");
            Object nmsPlayer = getHandle.invoke(player);
            Field playerConnectionField = nmsPlayer.getClass().getField("playerConnection");
            Object playerConnection = playerConnectionField.get(nmsPlayer);
            Method sendPacket = playerConnection.getClass().getMethod("sendPacket", getNmsClass("Packet"));
            sendPacket.invoke(playerConnection, packet);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    public static Class<?> getNmsClass(String nmsClassString) {
        try {
            String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
            String name = "net.minecraft.server." + version + nmsClassString;
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Class<?> getCraftBukkitClass(String craftBukkitClassString) {
        try {
            String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
            String name = "org.bukkit.craftbukkit." + version + craftBukkitClassString;
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

}
