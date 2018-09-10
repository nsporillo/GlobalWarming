package net.porillo.effect.negative;

import com.google.gson.JsonObject;
import net.porillo.effect.ClimateData;
import net.porillo.effect.api.ClimateEffect;
import net.porillo.effect.api.ClimateEffectType;
import net.porillo.effect.api.change.block.BlockChange;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Material;

import java.util.HashSet;
import java.util.function.Supplier;

@ClimateData(type = ClimateEffectType.SEA_LEVEL_RISE)
public class SeaLevelRise extends ClimateEffect<ChunkSnapshot, BlockChange>  {

	// TODO: Get Sea Level per model
	@Override
	public HashSet<BlockChange> execute(ChunkSnapshot snapshot) {
		return execute(snapshot, 64);
	}

	public HashSet<BlockChange> execute(ChunkSnapshot snapshot, int seaLevel) {
		SeaLevelRiseExecutor executor = new SeaLevelRiseExecutor(snapshot, seaLevel);
		return executor.get();
	}

	// TODO: Build a HashMap model from json object
	@Override
	public void setJsonModel(JsonObject jsonModel) {
		super.setJsonModel(jsonModel);
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
							blockChanges.add(new BlockChange(Material.AIR, Material.WATER, x, y + 1, z));
						}
					}
				}
			}

			return blockChanges;
		}
	}

}
