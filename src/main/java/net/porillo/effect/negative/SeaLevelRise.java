package net.porillo.effect.negative;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import net.porillo.GlobalWarming;
import net.porillo.effect.ClimateData;
import net.porillo.effect.api.AtomicClimateEffect;
import net.porillo.effect.api.ClimateEffectType;
import net.porillo.effect.api.change.block.BlockChange;
import net.porillo.engine.ClimateEngine;
import net.porillo.engine.api.WorldClimateEngine;
import net.porillo.util.MapUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.HashSet;
import java.util.TreeMap;
import java.util.function.Supplier;

@ClimateData(type = ClimateEffectType.SEA_LEVEL_RISE)
public class SeaLevelRise extends AtomicClimateEffect<ChunkSnapshot, BlockChange> {

    private TreeMap<Double, Integer> seaLevels;

    @Override
    public Supplier<HashSet<BlockChange>> execute(ChunkSnapshot snapshot) {
        World world = Bukkit.getWorld(snapshot.getWorldName());
        WorldClimateEngine climateEngine = ClimateEngine.getInstance().getClimateEngine(world.getUID());
        if (climateEngine != null && climateEngine.isEnabled()) {
            return execute(snapshot, MapUtil.searchTreeMap(seaLevels, climateEngine.getTemperature()));
        }

        return null;
    }

    public Supplier<HashSet<BlockChange>> execute(ChunkSnapshot snapshot, int seaLevel) {
        return new SeaLevelRiseExecutor(snapshot, seaLevel);
    }

    @Override
    public void setJsonModel(JsonObject jsonModel) {
        super.setJsonModel(jsonModel);
        seaLevels = GlobalWarming.getInstance().getGson().fromJson(jsonModel, new TypeToken<TreeMap<Double, Integer>>() {
        }.getType());
        if (seaLevels == null) {
            unregister();
        }
    }

    private class SeaLevelRiseExecutor implements Supplier<HashSet<BlockChange>> {

        private ChunkSnapshot snapshot;
        private int deltaSeaLevel;

        private SeaLevelRiseExecutor(ChunkSnapshot snapshot, int deltaSeaLevel) {
            this.snapshot = snapshot;
            this.deltaSeaLevel = deltaSeaLevel;
        }

        @Override
        public HashSet<BlockChange> get() {
            HashSet<BlockChange> blockChanges = new HashSet<>();
            World world = Bukkit.getWorld(snapshot.getWorldName());
            int baseSeaLevel = world.getSeaLevel() - 1;

            //Update the sea level for this block:
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    //Check that the base is water:
                    if (snapshot.getBlockData(x, baseSeaLevel, z).getMaterial() != Material.WATER) {
                        continue;
                    }

                    //Stack upward until the max is hit, or limited by non-air block:
                    int maxHeight = baseSeaLevel + deltaSeaLevel;
                    for (int y = baseSeaLevel; y < maxHeight; y++) {
                        Material topMaterial = snapshot.getBlockData(x, (y + 1), z).getMaterial();
                        if (topMaterial == Material.AIR) {
                            blockChanges.add(new BlockChange(Material.AIR, Material.WATER, x, (y + 1), z));
                        } else if (topMaterial != Material.WATER) {
                            break;
                        }
                    }
                }
            }

            return blockChanges;
        }
    }
}
