package net.porillo.effect.negative;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import lombok.Getter;
import net.porillo.GlobalWarming;
import net.porillo.effect.ClimateData;
import net.porillo.effect.api.ClimateEffectType;
import net.porillo.effect.api.ScheduleClimateEffect;
import net.porillo.engine.ClimateEngine;
import net.porillo.engine.api.FireDistribution;
import net.porillo.engine.api.WorldClimateEngine;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.Listener;

@ClimateData(type = ClimateEffectType.FIRE)
public class Fire extends ScheduleClimateEffect implements Listener {

    private static final int BLOCKS_PER_CHUNK = 16;
    @Getter private FireDistribution fireMap;

    /**
     * Set a random set of loaded blocks on fire
     */
    private void setFire(World world, int blocks) {
        if (world != null) {
            int count = world.getLoadedChunks().length;
            for (int i = 0; i < blocks; i++) {
                int chunkIndex = GlobalWarming.getInstance().getRandom().nextInt(count);
                Chunk chunk = world.getLoadedChunks()[chunkIndex];
                int x = (chunk.getX() * BLOCKS_PER_CHUNK) + GlobalWarming.getInstance().getRandom().nextInt(BLOCKS_PER_CHUNK);
                int z = (chunk.getZ() * BLOCKS_PER_CHUNK) + GlobalWarming.getInstance().getRandom().nextInt(BLOCKS_PER_CHUNK);
                Block topBlock = world.getHighestBlockAt(x, z);
                topBlock.getRelative(BlockFace.UP).setType(Material.FIRE);
            }
        }
    }

    /**
     * Periodically check if a fire should be set
     */
    @Override
    public void run() {
        for (World world : Bukkit.getWorlds()) {
            WorldClimateEngine climateEngine = ClimateEngine.getInstance().getClimateEngine(world.getUID());
            if (climateEngine != null &&
                  climateEngine.isEffectEnabled(ClimateEffectType.FIRE)) {
                double random = GlobalWarming.getInstance().getRandom().nextDouble();
                double chance = fireMap.getValue(climateEngine.getTemperature());
                if (random <= chance / 100.f) {
                    int blocks = (int) fireMap.getBlocks(climateEngine.getTemperature());
                    setFire(world, blocks);
                }
            }
        }
    }

    /**
     * Load the fire distribution model
     */
    @Override
    public void setJsonModel(JsonObject jsonModel) {
        super.setJsonModel(jsonModel);
        fireMap = GlobalWarming.getInstance().getGson().fromJson(
              jsonModel.get("distribution"),
              new TypeToken<FireDistribution>() {
              }.getType());

        if (fireMap == null) {
            unregister();
        } else {
            setPeriod(jsonModel.get("interval").getAsInt());
        }
    }
}
