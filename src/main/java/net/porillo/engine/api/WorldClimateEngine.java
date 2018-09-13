package net.porillo.engine.api;

import lombok.Getter;
import net.porillo.GlobalWarming;
import net.porillo.config.WorldConfig;
import net.porillo.database.tables.WorldTable;
import net.porillo.effect.api.ClimateEffectType;
import net.porillo.engine.models.*;
import net.porillo.objects.Contribution;
import net.porillo.objects.Furnace;
import net.porillo.objects.Reduction;
import net.porillo.objects.Tree;
import org.bukkit.TreeType;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class WorldClimateEngine {

	private final String worldName;

	@Getter private WorldConfig config;

	// Models
	private ScoreTempModel scoreTempModel;
	private ContributionModel contributionModel;
	private ReductionModel reductionModel;
	@Getter private EntityFitnessModel entityFitnessModel;
	@Getter private CarbonIndexModel carbonIndexModel;

	public WorldClimateEngine(WorldConfig config) {
		this.worldName = config.getWorld();
		this.config = config;
		// Worlds load their own model file
		this.scoreTempModel = new ScoreTempModel(worldName);
		this.contributionModel = new ContributionModel(worldName);
		this.reductionModel = new ReductionModel(worldName);
		this.entityFitnessModel = new EntityFitnessModel(worldName);
		this.carbonIndexModel = new CarbonIndexModel(worldName);
	}

	public Reduction treeGrow(Tree tree, TreeType treeType, List<BlockState> blocks) {
		int reductionValue = 0;

		for (BlockState bs : blocks) {
			reductionValue += reductionModel.getReduction(bs.getType());
		}

		Integer uniqueId = GlobalWarming.getInstance().getRandom().nextInt(Integer.MAX_VALUE);
		Reduction reduction = new Reduction();
		reduction.setUniqueID(uniqueId);
		reduction.setWorldName(worldName);
		reduction.setReductioner(tree.getOwnerID());
		reduction.setReductionKey(tree.getUniqueID());
		reduction.setReductionValue(reductionValue);
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

	public boolean isEffectEnabled(ClimateEffectType type) {
		return isEnabled() && config.getWorld().equals(worldName) && config.getEnabledEffects().contains(type);
	}

	public boolean isEnabled() {
		return this.config.isEnabled();
	}
}
