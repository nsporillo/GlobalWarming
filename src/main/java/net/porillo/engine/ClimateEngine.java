package net.porillo.engine;

import net.porillo.engine.models.ContributionModel;
import net.porillo.engine.models.EntityFitnessModel;
import net.porillo.engine.models.ScoreTempModel;
import net.porillo.objects.Contribution;
import net.porillo.objects.GPlayer;
import net.porillo.objects.GWorld;
import net.porillo.objects.Reduction;
import org.bukkit.TreeType;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public class ClimateEngine {

	private GWorld world;
	
	// Models 
	private ScoreTempModel scoreTempModel;
	private ContributionModel contributionModel;
	private EntityFitnessModel entityFitnessModel;

	public ClimateEngine(GWorld world) {
		this.world = world;
		this.scoreTempModel = new ScoreTempModel();
		this.contributionModel = new ContributionModel();
		this.entityFitnessModel = new EntityFitnessModel();
	}

	public Reduction treeGrow(GPlayer player, TreeType treeType, List<BlockState> blocks) {
		// TODO: Add ReductionModel 
		// For now, we use a flat reduction rate proportional to number of blocks which grew
		Reduction reduction = new Reduction();
		reduction.setUniqueID(UUID.randomUUID());
		reduction.setReductioner(player.getUuid());
		reduction.setWorldName(world.getWorldName());
		reduction.setReductionValue(blocks.size());
		return reduction;
	}

    public Contribution furnaceBurn(GPlayer player, ItemStack fuel) {
    	String fuelType = fuel.getType().name().toLowerCase();
		Contribution contribution = new Contribution();
		contribution.setUniqueID(UUID.randomUUID());
		contribution.setWorldName(world.getWorldName());
        contribution.setContributionKey(player.getUuid());
		contribution.setContributionValue(contributionModel.getContribution(fuelType));
		return contribution;
	}

	public Double getTemperature() {
		return scoreTempModel.getTemperature(world.getCarbonValue());
	}
}
