package dev.tranducminh.witherlitmit;

import org.bukkit.Chunk;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Wither;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class WitherLitmit extends JavaPlugin implements Listener {

    private Map<Chunk, LinkedHashMap<Long, Wither>> witherMap;
    private int maxWithersPerChunk;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadConfig();

        witherMap = new HashMap<>();
        maxWithersPerChunk = getConfig().getInt("maxWitherChunk", 2);

        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onWitherSpawn(EntitySpawnEvent event) {
        if (event.getEntityType() == EntityType.WITHER) {
            Wither wither = (Wither) event.getEntity();
            Chunk chunk = wither.getLocation().getChunk();

            if (!witherMap.containsKey(chunk)) {
                witherMap.put(chunk, new LinkedHashMap<>());
            }

            LinkedHashMap<Long, Wither> chunkWithers = witherMap.get(chunk);

            long currentMillis = System.currentTimeMillis();
            chunkWithers.put(currentMillis, wither);

            if (chunkWithers.size() > maxWithersPerChunk) {
                int excess = chunkWithers.size() - maxWithersPerChunk;
                for (int i = 0; i < excess; i++) {
                    Long oldestKey = chunkWithers.keySet().iterator().next();
                    Wither oldestWither = chunkWithers.remove(oldestKey);
                    oldestWither.remove();
                }
            }
        }
    }
}