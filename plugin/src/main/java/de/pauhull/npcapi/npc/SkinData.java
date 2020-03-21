package de.pauhull.npcapi.npc;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.pauhull.npcapi.NpcApi;
import de.pauhull.npcapi.util.TimedHashMap;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Created by Paul
 * on 03.02.2019
 *
 * @author pauhull
 */
public class SkinData {

    private static TimedHashMap<UUID, SkinData> cache = new TimedHashMap<>(TimeUnit.HOURS, 12);
    private final String texture, signature;

    public SkinData(String texture, String signature) {
        this.texture = texture;
        this.signature = signature;
    }

    public static TimedHashMap<UUID, SkinData> getCache() {
        return cache;
    }

    public static SkinData getSkinSync(UUID uuid) {
        if (cache.containsKey(uuid)) {
            return cache.get(uuid);
        }

        try {
            URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.toString().replace("-", "") + "?unsigned=false");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            JsonElement element = new JsonParser().parse(new InputStreamReader(connection.getInputStream()));
            JsonObject rootObject = element.getAsJsonObject();

            if (rootObject.has("error")) {
                // too many requests :(
                return null;
            }

            JsonArray propertiesArray = rootObject.get("properties").getAsJsonArray();
            for (JsonElement property : propertiesArray) {
                JsonObject propertyObject = property.getAsJsonObject();

                if (!propertyObject.has("name") || !propertyObject.get("name").getAsString().equals("textures")) {
                    continue;
                }

                String value = propertyObject.get("value").getAsString();
                String signature = propertyObject.get("signature").getAsString();
                return new SkinData(value, signature);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void getSkinAsync(UUID uuid, Consumer<SkinData> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(NpcApi.getInstance(), () -> consumer.accept(getSkinSync(uuid)));
    }

    public String getTexture() {
        return texture;
    }

    public String getSignature() {
        return signature;
    }
}
