package net.porillo.effect.negative;

import net.porillo.GlobalWarming;
import net.porillo.effect.api.ClimateEffect;
import net.porillo.effect.api.change.block.BlockChange;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Material;
import org.bukkit.block.Biome;

import java.util.HashSet;

public class SeaLevelRise extends ClimateEffect<BlockChange> {

	private transient ChunkSnapshot snapshot;
	private transient int seaLevel;

	public SeaLevelRise(ChunkSnapshot snapshot, int seaLevel) {
		this.snapshot = snapshot;
		this.seaLevel = seaLevel;
		super.setEffectName("SeaLevelRise");
	}

	@Override
	public HashSet<BlockChange> get() {
		HashSet<BlockChange> blockChanges = new HashSet<>();

		// loop all x,z coords in the chunk
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				int y = seaLevel;
				final Material blockType = snapshot.getBlockData(x, y, z).getMaterial();
				final Biome biome = snapshot.getBiome(x,z);

				// Skip sea level rise in non-oceans and non-rivers
				if (!biome.name().contains("OCEAN") && !biome.name().contains("RIVER")) {
					GlobalWarming.getInstance().getLogger().info(String.format("Skipping (%d,%d,%d)", x, y, z));
					break;
				}

				// If the block at "sea level" is water, check above to see if it's air
				if (blockType == Material.WATER) {
					final Material aboveBlockType = snapshot.getBlockData(x, (y + 1), z).getMaterial();

					if (aboveBlockType == Material.AIR) {
						blockChanges.add(new BlockChange(Material.AIR, Material.WATER, x, y + 1, z));
						break;
					}
				}
			}
		}

		return blockChanges;
	}
}
