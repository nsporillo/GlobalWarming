package net.porillo.effect.negative;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import lombok.Getter;
import lombok.Setter;
import net.porillo.GlobalWarming;
import net.porillo.effect.ClimateData;
import net.porillo.effect.api.ClimateEffectType;
import net.porillo.effect.api.ListenerClimateEffect;
import net.porillo.engine.ClimateEngine;
import net.porillo.engine.api.Distribution;
import net.porillo.engine.api.WorldClimateEngine;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Sea-level rise
 *  - Two asynchronous, repeating tasks
 *  1) Add jobs to the stack (once the stack is empty)
 *  2) Apply any required changes
 *
 *  - Sea level will rise with the temperature
 *  - Raised blocks are tagged with meta data
 *  - When sea levels lower, the tagged blocks are reset
 *  - Will not dry out lakes, rivers, irrigation, machines, etc.
 *  - Considerations made for growing kelp, player changes, and
 *    other events: blocks that drop, etc.
 */
@ClimateData(type = ClimateEffectType.SEA_LEVEL_RISE)
public class SeaLevelRise extends ListenerClimateEffect {

    @Getter private Distribution seaMap;
    @Getter @Setter private boolean isOverride;
    private int chunkTicks;
    private int chunksPerPeriod;
    private int queueTicks;
    private ConcurrentLinkedQueue<ChunkSnapshot> requestQueue;
    private int maxTemperature;
    private static final MetadataValue BLOCK_TAG = new FixedMetadataValue(GlobalWarming.getInstance(), true);
    private static final String SEALEVEL_BLOCK = "S";

    public SeaLevelRise() {
        isOverride = false;
        requestQueue = new ConcurrentLinkedQueue<>();
    }

    /**
     * Update the queue with loaded-chunks one the queue is empty
     */
    private void startQueueLoader() {
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(
              GlobalWarming.getInstance(),
              () -> {
                  synchronized (this) {
                      if (requestQueue.isEmpty()) {
                          for (World world : Bukkit.getWorlds()) {
                              final WorldClimateEngine worldClimateEngine =
                                    ClimateEngine.getInstance().getClimateEngine(world.getUID());

                              if (worldClimateEngine != null &&
                                    worldClimateEngine.isEffectEnabled(ClimateEffectType.SEA_LEVEL_RISE) &&
                                    world.getPlayers().size() > 0) {
                                  for (Chunk chunk : world.getLoadedChunks()) {
                                      requestQueue.add(chunk.getChunkSnapshot());
                                  }
                              }
                          }
                      }
                  }
              }, 0L, queueTicks);
    }

    /**
     * Update the chunks when requests are available
     */
    private void debounceChunkUpdates() {
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(
              GlobalWarming.getInstance(),
              () -> {
                  //Make a copy of the items removed from the queue:
                  // - Synchronized to temporarily prevent threaded additions
                  Queue<ChunkSnapshot> chunkSnapshots = null;
                  synchronized (this) {
                      if (!requestQueue.isEmpty()) {
                          chunkSnapshots = new ConcurrentLinkedQueue<>();
                          while (chunkSnapshots.size() < chunksPerPeriod && !requestQueue.isEmpty()) {
                              chunkSnapshots.add(requestQueue.poll());
                          }
                      }
                  }

                  //Process blocks in each chunk:
                  if (chunkSnapshots != null) {
                      for (ChunkSnapshot chunkSnapshot : chunkSnapshots) {
                          updateChunk(chunkSnapshot);
                      }
                  }
              }, 0L, chunkTicks);
    }

    /**
     * Updates the sea level for the given chunk (up or down)
     * - BlockFromToEvent is the key to making this work:
     * - RISING SEAS: helps identify which blocks were created and which were pre-existing lakes, rivers, irrigation, etc.
     * - EBBING SEAS: prevents pending chunks from spilling into cleared chunks
     */
    private void updateChunk(ChunkSnapshot snapshot) {
        //Setup:
        World world = Bukkit.getWorld(snapshot.getWorldName());
        WorldClimateEngine climateEngine = ClimateEngine.getInstance().getClimateEngine(world.getUID());
        final int baseSeaLevel = world.getSeaLevel() - 1;
        final int deltaSeaLevel = (int) seaMap.getValue(climateEngine.getTemperature());
        final int customSeaLevel = baseSeaLevel + deltaSeaLevel;
        final int maxHeight = baseSeaLevel + (int) seaMap.getValue(maxTemperature);

        //Scan chunk-blocks within the sea-level's range:
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = baseSeaLevel; y < maxHeight; y++) {
                    Block block = world.getChunkAt(snapshot.getX(), snapshot.getZ()).getBlock(x, y, z);
                    if (y <= customSeaLevel) {
                        //BELOW CUSTOM SEA LEVEL
                        // - Rising the sea level (AIR to WATER)
                        // - Fill any air pockets below sea level
                        if (block.getType() == Material.AIR) {
                            block.setType(Material.WATER, false);
                            block.setMetadata(SEALEVEL_BLOCK, BLOCK_TAG);
                        }
                    } else {
                        if (block.hasMetadata(SEALEVEL_BLOCK)) {
                            //ABOVE CUSTOM SEA LEVEL AND TAGGED
                            // - Lowering the sea level (WATER TO AIR)
                            // - Remove tagged water blocks above the custom sea level
                            // - Update selected block-types only in case of unexpected changes
                            if (block.getType() == Material.WATER ||
                                  block.getType() == Material.ICE ||
                                  block.getType() == Material.PACKED_ICE ||
                                  block.getType() == Material.TALL_SEAGRASS ||
                                  block.getType() == Material.KELP_PLANT) {
                                block.setType(Material.AIR, false);
                            }

                            block.removeMetadata(SEALEVEL_BLOCK, GlobalWarming.getInstance());
                        } else if (isOverride) {
                            //ABOVE CUSTOM SEA LEVEL AND REPAIRING (i.e., after a server reload)
                            // - Re-tag all blocks so water doesn't fill back into removed blocks
                            // - Note: this is destructive (i.e., leads to the removal any pre-existing water
                            //   between sea level and the maximum config height)
                            if (block.getType() == Material.WATER ||
                                  block.getType() == Material.ICE ||
                                  block.getType() == Material.PACKED_ICE ||
                                  block.getType() == Material.TALL_SEAGRASS ||
                                  block.getType() == Material.KELP_PLANT) {
                                block.setMetadata(SEALEVEL_BLOCK, BLOCK_TAG);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Replacing a sea-level-block with will remove it from the set
     */
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getBlock().hasMetadata(SEALEVEL_BLOCK)) {
            event.getBlock().removeMetadata(SEALEVEL_BLOCK, GlobalWarming.getInstance());
        }
    }

    /**
     * Emptying a bucket (water or lava) will remove the adjacent block
     * from the sea-level-block set
     */
    @EventHandler
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        Block adjacent = event.getBlockClicked().getRelative(event.getBlockFace());
        if (adjacent.hasMetadata(SEALEVEL_BLOCK)) {
            adjacent.removeMetadata(SEALEVEL_BLOCK, GlobalWarming.getInstance());
        }
    }

    /**
     * This is the game-changer event:
     * - Prevents the sea-level-rise blocks from filling other areas
     * - This reduces all of the confusion as to which blocks are ours,
     * and which were pre-existing
     */
    @EventHandler
    public void onBlockFromToEvent(BlockFromToEvent event) {
        if (event.getBlock().hasMetadata(SEALEVEL_BLOCK)) {
            event.setCancelled(true);
        }
    }

    /**
     * Load the sea-level distribution model
     */
    @Override
    public void setJsonModel(JsonObject jsonModel) {
        super.setJsonModel(jsonModel);
        seaMap = GlobalWarming.getInstance().getGson().fromJson(
              jsonModel.get("distribution"),
              new TypeToken<Distribution>() {
              }.getType());

        if (seaMap == null) {
            unregister();
        } else {
            maxTemperature = (int) seaMap.getTemp()[seaMap.getTemp().length - 1];
            chunkTicks = jsonModel.get("chunk-ticks").getAsInt();
            chunksPerPeriod = jsonModel.get("chunks-per-period").getAsInt();
            queueTicks = jsonModel.get("queue-ticks").getAsInt();
            startQueueLoader();
            debounceChunkUpdates();
        }
    }
}
