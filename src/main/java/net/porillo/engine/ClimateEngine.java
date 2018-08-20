package net.porillo.engine;

import net.porillo.objects.Contribution;
import net.porillo.objects.Reduction;
import net.porillo.objects.World;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.world.StructureGrowEvent;

import java.util.UUID;

public class ClimateEngine {

	private ScoreTempModel scoreTempModel;
	private ContributionModel contributionModel;
	private World world;

	public ClimateEngine(World world) {
		this.world = world;
		this.scoreTempModel = new ScoreTempModel();
		this.contributionModel = new ContributionModel();
	}

	public Reduction treeGrow(StructureGrowEvent event) {
		return null;
	}

	public Contribution furnaceBurn(FurnaceBurnEvent event) {
		Contribution contribution = new Contribution();
		contribution.setUniqueID(UUID.randomUUID());

		contribution.setWorldName(world.getWorldName());
		contribution.setContributionValue(contributionModel.getContribution(event.getFuel().getType().name().toLowerCase()));
	}

	public Double getTemperature() {
		return scoreTempModel.getTemperature(world.getScore());
	}
}
