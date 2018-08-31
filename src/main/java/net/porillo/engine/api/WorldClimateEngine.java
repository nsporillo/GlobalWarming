package net.porillo.engine.api;

import net.porillo.GlobalWarming;
import net.porillo.engine.models.ContributionModel;
import net.porillo.engine.models.EntityFitnessModel;
import net.porillo.engine.models.ScoreTempModel;
import net.porillo.objects.*;
import org.bukkit.TreeType;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class WorldClimateEngine {

	private GWorld world;

	// Models
	private ScoreTempModel scoreTempModel;
	private ContributionModel contributionModel;
	private EntityFitnessModel entityFitnessModel;

	public WorldClimateEngine(GWorld world) {
		this.world = world;
		//TODO: Make each world load it's own model file
		this.scoreTempModel = new ScoreTempModel();
		this.contributionModel = new ContributionModel();
		this.entityFitnessModel = new EntityFitnessModel();
	}

	public Reduction treeGrow(Tree tree, TreeType treeType, List<BlockState> blocks) {
		// TODO: Add ReductionModel
		// For now, we use a flat reduction rate proportional to number of blocks which grew
		Long uniqueId = GlobalWarming.getInstance().getRandom().nextLong();
		Reduction reduction = new Reduction();
		reduction.setUniqueID(uniqueId);
		reduction.setWorldName(world.getWorldName());
		reduction.setReductioner(tree.getOwner().getUniqueId());
		reduction.setReductionKey(tree.getUniqueID());
		reduction.setReductionValue(blocks.size());
		return reduction;
	}

	public Contribution furnaceBurn(Furnace furnace, ItemStack fuel) {
		Long uniqueId = GlobalWarming.getInstance().getRandom().nextLong();
		Contribution contribution = new Contribution();
		contribution.setUniqueID(uniqueId);
		contribution.setWorldName(world.getWorldName());
		contribution.setContributer(furnace.getOwner().getUniqueId());
		contribution.setContributionKey(furnace.getUniqueID());
		contribution.setContributionValue(contributionModel.getContribution(fuel.getType()));
		return contribution;
	}

	public Double getTemperature() {
		return scoreTempModel.getTemperature(world.getCarbonValue());
	}
}
