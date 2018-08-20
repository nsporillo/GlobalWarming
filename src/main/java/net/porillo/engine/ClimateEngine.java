package net.porillo.engine;

import java.util.List;
import java.util.UUID;

import org.bukkit.TreeType;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.ItemStack;

import net.porillo.objects.Contribution;
import net.porillo.objects.Player;
import net.porillo.objects.Reduction;
import net.porillo.objects.World;

public class ClimateEngine {

	private ScoreTempModel scoreTempModel;
	private ContributionModel contributionModel;
	private World world;

	public ClimateEngine(World world) {
		this.world = world;
		this.scoreTempModel = new ScoreTempModel();
		this.contributionModel = new ContributionModel();
	}

	public Reduction treeGrow(Player player, TreeType treeType, List<BlockState> blocks ) {
		// TODO: Add ReductionModel 
		// For now, we use a flat reduction rate proportional to number of blocks which grew
		Reduction reduction = new Reduction();
		reduction.setUniqueID(UUID.randomUUID());
		reduction.setReductioner(player.getUuid());
		reduction.setWorldName(world.getWorldName());
		reduction.setReductionValue(blocks.size());
		return reduction;
	}

	public Contribution furnaceBurn(Player player, ItemStack fuel) {
		Contribution contribution = new Contribution();
		contribution.setUniqueID(UUID.randomUUID());
		contribution.setWorldName(world.getWorldName());
		contribution.setContributionKey(player.getUuid());
		contribution.setContributionValue(contributionModel.getContribution(fuel.getType().name().toLowerCase()));
		return contribution;
	}

	public Double getTemperature() {
		return scoreTempModel.getTemperature(world.getScore());
	}
}
