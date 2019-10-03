package net.porillo.effect.negative;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import net.porillo.GlobalWarming;
import net.porillo.effect.ClimateData;
import net.porillo.effect.api.ClimateEffectType;
import net.porillo.effect.api.ListenerClimateEffect;
import net.porillo.engine.ClimateEngine;
import net.porillo.engine.api.Distribution;
import net.porillo.engine.api.WorldClimateEngine;
import net.porillo.objects.GChunk;
import net.porillo.util.ChunkSorter;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.World;
import org.bukkit.block.Biome;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

@ClimateData(type = ClimateEffectType.CORAL_DEATH)
public class CoralDeath extends ListenerClimateEffect {

    private static Set<Biome> IMPACTED_BIOMES = new HashSet<>();
    private final Map<GChunk, Long> lastChanged = new HashMap<>();
    private final ConcurrentLinkedQueue<ChunkSnapshot> requestQueue;
    private final Instant now;

    private Distribution distribution;
    private int chunkTicks, chunksPerPeriod, queueTicks;

    static {
        IMPACTED_BIOMES.add(Biome.WARM_OCEAN);
        IMPACTED_BIOMES.add(Biome.DEEP_WARM_OCEAN);
    }

    public CoralDeath() {
        this.requestQueue = new ConcurrentLinkedQueue<>();
        this.now = Instant.now();
    }

    private void startQueueLoader() {
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(
                GlobalWarming.getInstance(),
                () -> {
                    for (World world : Bukkit.getWorlds()) {
                        if (world.getEnvironment() != World.Environment.NORMAL) {
                            continue;
                        }
                        final WorldClimateEngine wce = ClimateEngine.getInstance().getClimateEngine(world.getUID());

                        if (wce != null && wce.isEffectEnabled(ClimateEffectType.CORAL_DEATH)) {
                            if (distribution.getValue(wce.getTemperature()) == 100) continue;

                            for (Chunk chunk : ChunkSorter.quickDistanceChunks(world.getLoadedChunks(), world.getPlayers(), chunksPerPeriod)) {
                                GChunk gChunk = new GChunk(chunk);
                                if (!lastChanged.containsKey(gChunk)) {
                                    lastChanged.put(gChunk, 0L);
                                } else {
                                    if (now.min)
                                }
                                ChunkSnapshot snapshot = chunk.getChunkSnapshot();
                                if (IMPACTED_BIOMES.contains(snapshot.getBiome(7,7))) {
                                    requestQueue.add(snapshot);
                                }
                            }
                        }
                    }

                }, 0L, queueTicks);
    }

    private void debounceChunkUpdates() {
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(
                GlobalWarming.getInstance(),
                () -> {
                    int chunks = 0;
                    while (chunks < chunksPerPeriod && !requestQueue.isEmpty()) {
                        updateChunk(requestQueue.poll());
                        chunks++;
                    }
                }, 0L, chunkTicks);
    }

    private void updateChunk(ChunkSnapshot poll) {

    }

    /**
     * Load the sea-level distribution model
     */
    @Override
    public void setJsonModel(JsonObject jsonModel) {
        super.setJsonModel(jsonModel);

        try {
            distribution = GlobalWarming.getInstance().getGson().fromJson(
                    jsonModel.get("distribution"),
                    new TypeToken<Distribution>() {
                    }.getType());

            if (jsonModel.has("chunk-ticks")) {
                chunkTicks = jsonModel.get("chunk-ticks").getAsInt();
            } else {
                GlobalWarming.getInstance().getLogger().info("chunk-ticks not defined for CoralDeath. Defaulting to 20");
                chunkTicks = 20;
            }

            if (jsonModel.has("chunks-per-period")) {
                chunksPerPeriod = jsonModel.get("chunks-per-period").getAsInt();
            } else {
                GlobalWarming.getInstance().getLogger().info("chunks-per-period not defined for CoralDeath. Defaulting to 32");
                chunksPerPeriod = 32;
            }

            if (jsonModel.has("queue-ticks")) {
                queueTicks = jsonModel.get("queue-ticks").getAsInt();
            } else {
                GlobalWarming.getInstance().getLogger().info("queue-ticks not defined for CoralDeath. Defaulting to 40");
                queueTicks = 40;
            }

            startQueueLoader();
            debounceChunkUpdates();
        } catch (Exception ex) {
            unregister();
        }
    }
}
