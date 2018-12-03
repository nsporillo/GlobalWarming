package net.porillo.effect.api.change.block;

import lombok.AllArgsConstructor;
import net.porillo.effect.api.change.block.BlockChange;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@AllArgsConstructor
public class SyncChunkUpdateTask extends BukkitRunnable {

	private Chunk chunk;
	private CompletableFuture<HashSet<BlockChange>> results;

	@Override
	public void run() {
		try {
			HashSet<BlockChange> changes = results.get();
			for (BlockChange change : changes) {
				Block block = chunk.getBlock(change.getX(), change.getY(), change.getZ());
				//TODO: Add some more logic to ensure this works perfectly.
				// Currently works, but there might be edge cases not considered when written
				block.setType(change.getNewType(), true);
			}
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}
}
