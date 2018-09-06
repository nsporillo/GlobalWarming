package net.porillo.engine.api;

import lombok.Getter;
import net.porillo.GlobalWarming;
import net.porillo.database.tables.WorldTable;
import net.porillo.engine.models.CarbonIndexModel;
import net.porillo.engine.models.ContributionModel;
import net.porillo.engine.models.EntityFitnessModel;
import net.porillo.engine.models.ScoreTempModel;
import net.porillo.objects.*;
import org.bukkit.TreeType;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class WorldClimateEngine {

	private String worldName;

	// Models
	private ScoreTempModel scoreTempModel;
	private ContributionModel contributionModel;
	private EntityFitnessModel entityFitnessModel;
	@Getter private CarbonIndexModel carbonIndexModel;

	public WorldClimateEngine(String worldName) {
		this.worldName = worldName;
		//TODO: Make each world load it's own model file
		this.scoreTempModel = new ScoreTempModel();
		this.contributionModel = new ContributionModel();
		this.entityFitnessModel = new EntityFitnessModel();
		this.carbonIndexModel = new CarbonIndexModel();
	}

	public Reduction treeGrow(Tree tree, TreeType treeType, List<BlockState> blocks) {
		// TODO: Add ReductionModel
		// For now, we use a flat reduction rate proportional to number of blocks which grew
		Integer uniqueId = GlobalWarming.getInstance().getRandom().nextInt(Integer.MAX_VALUE);
		Reduction reduction = new Reduction();
		reduction.setUniqueID(uniqueId);
		reduction.setWorldName(worldName);
		reduction.setReductioner(tree.getOwnerID());
		reduction.setReductionKey(tree.getUniqueID());
		reduction.setReductionValue(blocks.size());
		return reduction;
	}

	public Contribution furnaceBurn(Furnace furnace, ItemStack fuel) {
		if (furnace == null) {
			GlobalWarming.getInstance().getLogger().severe("Furnace null");
		}

		if (fuel == null) {
			GlobalWarming.getInstance().getLogger().severe("Fuel null");
		}

		Integer uniqueId = GlobalWarming.getInstance().getRandom().nextInt(Integer.MAX_VALUE);
		Contribution contribution = new Contribution();
		contribution.setUniqueID(uniqueId);
		contribution.setWorldName(worldName);
		contribution.setContributer(furnace.getOwnerID());
		contribution.setContributionKey(furnace.getUniqueID());
		contribution.setContributionValue((int) contributionModel.getContribution(fuel.getType()));
		return contribution;
	}

	public Double getTemperature() {
		WorldTable worldTable = GlobalWarming.getInstance().getTableManager().getWorldTable();
		return scoreTempModel.getTemperature(worldTable.getWorld(worldName).getCarbonValue());
	}
}
