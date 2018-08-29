package net.porillo.effects;

import lombok.AllArgsConstructor;
import net.porillo.GlobalWarming;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class EffectEngine {

	private static EffectEngine effectEngine;

	public void processChunk(World world, SeaLevelRise damageEffect) {
		// Kickoff async chunk processing
		final CompletableFuture<HashSet<BlockChange>> result = CompletableFuture.supplyAsync(damageEffect);
		// Queue main thread to try to get the results in 2 seconds (will block thereafter)
		new ApplyChangesTask(world, result).runTaskLater(GlobalWarming.getInstance() , 40L);
	}

	@AllArgsConstructor
	private static class ApplyChangesTask extends BukkitRunnable {

		World world;
		CompletableFuture<HashSet<BlockChange>> results;

		@Override
		public void run() {
			try {
				world = Bukkit.getWorld(world.getName());
				HashSet<BlockChange> changes = results.get();
				GlobalWarming.getInstance().getLogger().info("BlockChanges: " + changes.size());
				for (BlockChange change : changes) {
					Block block = world.getBlockAt(change.getX(), change.getY(), change.getZ());
					//TODO: Add some more logic to ensure this works perfectly.
					// Currently works, but there might be edge cases not considered when written
					block.setType(change.getNewType());
				}
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}
	}

	public static EffectEngine getInstance() {
		if (effectEngine == null) {
			return new EffectEngine();
		}

		return effectEngine;
	}
}
