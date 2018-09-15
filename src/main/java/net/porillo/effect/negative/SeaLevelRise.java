package net.porillo.effect.negative;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import net.porillo.GlobalWarming;
import net.porillo.effect.ClimateData;
import net.porillo.effect.api.AtomicClimateEffect;
import net.porillo.effect.api.ClimateEffectType;
import net.porillo.effect.api.change.block.BlockChange;
import net.porillo.engine.ClimateEngine;
import net.porillo.util.MapUtil;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Material;

import java.util.Collection;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.function.Supplier;

@ClimateData(type = ClimateEffectType.SEA_LEVEL_RISE)
public class SeaLevelRise extends AtomicClimateEffect<ChunkSnapshot, BlockChange> {

	private TreeMap<Double, Integer> seaLevels;

	@Override
	public Supplier<HashSet<BlockChange>> execute(ChunkSnapshot snapshot) {
		double temp = ClimateEngine.getInstance().getClimateEngine(snapshot.getWorldName()).getTemperature();
		return execute(snapshot, MapUtil.searchTreeMap(seaLevels, temp));
	}

	public Supplier<HashSet<BlockChange>> execute(ChunkSnapshot snapshot, int seaLevel) {
		return new SeaLevelRiseExecutor(snapshot, seaLevel);
	}

	@Override
	public void setJsonModel(JsonObject jsonModel) {
		super.setJsonModel(jsonModel);
		seaLevels = GlobalWarming.getInstance().getGson().fromJson(jsonModel, new TypeToken<TreeMap<Double, Integer>>(){}.getType());
		if (seaLevels == null) {
			unregister();
		}
	}

	private class SeaLevelRiseExecutor implements Supplier<HashSet<BlockChange>> {

		private ChunkSnapshot snapshot;
		private int seaLevel;

		private SeaLevelRiseExecutor(ChunkSnapshot snapshot, int seaLevel) {
			this.snapshot = snapshot;
			this.seaLevel = seaLevel;
		}

		@Override
		public HashSet<BlockChange> get() {
			HashSet<BlockChange> blockChanges = new HashSet<>();

			// loop all x,z coords in the chunk
			for (int x = 0; x < 16; x++) {
				for (int z = 0; z < 16; z++) {
					int y = seaLevel;
					final Material blockType = snapshot.getBlockData(x, y, z).getMaterial();

					// If the block at "sea level" is water, check above to see if it's air
					if (blockType == Material.WATER) {
						final Material aboveBlockType = snapshot.getBlockData(x, (y + 1), z).getMaterial();

						if (aboveBlockType == Material.AIR) {
							blockChanges.add(new BlockChange(Material.AIR, Material.WATER, x, y, z));
						}
					}
				}
			}

			return blockChanges;
		}
	}

}
