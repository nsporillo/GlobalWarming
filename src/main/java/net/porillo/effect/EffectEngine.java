package net.porillo.effect;

import net.porillo.GlobalWarming;
import net.porillo.effect.api.ClimateEffect;
import net.porillo.effect.api.SyncChunkUpdateTask;
import net.porillo.effect.api.change.block.BlockChange;
import net.porillo.effect.negative.SeaLevelRise;
import net.porillo.engine.ClimateEngine;
import net.porillo.engine.api.WorldClimateEngine;
import net.porillo.objects.GWorld;
import org.bukkit.Chunk;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class EffectEngine {

	private static EffectEngine effectEngine;

	//TODO: Load the list of climate effects from the WorldClimateEngine object
	// Each world should have their own model file
	private List<ClimateEffect> effects;
	private double minTemp;

	public EffectEngine() {
		this.effects = new ArrayList<>();
	}

	public void processChunk(World world, Chunk chunk) {
		WorldClimateEngine worldClimateEngine = ClimateEngine.getInstance().getClimateEngine(world.getName());

		if (worldClimateEngine != null) {
			GWorld gWorld = GlobalWarming.getInstance().getTableManager().getWorldTable().getWorld(world.getName());

			if (gWorld.getTemperature() >= minTemp) {
				for (ClimateEffect effect : effects) {
					double tempDiff = gWorld.getTemperature() - effect.getEffectThreshold();

					if (tempDiff > 0) {
						if (effect.getEffectName().equals("SeaLevelRise")) {
							SeaLevelRise seaLevelRise = new SeaLevelRise(chunk.getChunkSnapshot(), gWorld.getSeaLevel());
							final CompletableFuture<HashSet<BlockChange>> result = CompletableFuture.supplyAsync(seaLevelRise);
							new SyncChunkUpdateTask(chunk, result).runTaskLater(GlobalWarming.getInstance(), 40L);
						} else {
							//TODO: Add all effects
						}
					}
				}
			}
		}
	}

	public void testSeaLevelRise(Chunk chunk, int seaLevel) {
		SeaLevelRise seaLevelRise = new SeaLevelRise(chunk.getChunkSnapshot(), seaLevel);
		final CompletableFuture<HashSet<BlockChange>> result = CompletableFuture.supplyAsync(seaLevelRise);
		new SyncChunkUpdateTask(chunk, result).runTaskLater(GlobalWarming.getInstance(), 40L);
	}

	public static EffectEngine getInstance() {
		if (effectEngine == null) {
			return new EffectEngine();
		}

		return effectEngine;
	}
}
