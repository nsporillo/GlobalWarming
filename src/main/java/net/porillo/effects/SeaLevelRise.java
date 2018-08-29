package net.porillo.effects;

import lombok.AllArgsConstructor;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Material;

import java.util.HashSet;
import java.util.function.Supplier;

@AllArgsConstructor
public class SeaLevelRise implements Supplier<HashSet<BlockChange>> {

	private final ChunkSnapshot snapshot;

	@Override
	public HashSet<BlockChange> get() {
		HashSet<BlockChange> blockChanges = new HashSet<>();
		final int chunkX = snapshot.getX() << 4;
		final int chunkZ = snapshot.getZ() << 4;

		// loop all x,z coords in the chunk
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {

				// Find the highest water block and queue a one block sea level rise
				for (int y = 60; y < 70; y++) {
					final Material blockType = snapshot.getBlockData(x, y, z).getMaterial();

					// Find the existing sea level
					if (blockType == Material.WATER) {
						final Material aboveBlockType = snapshot.getBlockData(x, (y + 1), z).getMaterial();

						if (aboveBlockType == Material.AIR) {
							blockChanges.add(new BlockChange(Material.AIR, Material.WATER, chunkX + x, y + 1, chunkZ + z));
							break;
						}
					}
				}
			}
		}

		return blockChanges;
	}
}
