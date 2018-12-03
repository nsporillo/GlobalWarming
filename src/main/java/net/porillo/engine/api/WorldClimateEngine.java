package net.porillo.engine.api;

import lombok.Getter;
import net.porillo.GlobalWarming;
import net.porillo.config.WorldConfig;
import net.porillo.database.tables.WorldTable;
import net.porillo.effect.api.ClimateEffectType;
import net.porillo.engine.models.*;
import net.porillo.objects.*;
import org.bukkit.TreeType;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class WorldClimateEngine {

	@Getter private WorldConfig config;

	// Models
	@Getter private ScoreTempModel scoreTempModel;
	private ContributionModel contributionModel;
	private ReductionModel reductionModel;
	@Getter private EntityFitnessModel entityFitnessModel;
	@Getter private CarbonIndexModel carbonIndexModel;

	public WorldClimateEngine(WorldConfig config) {
		this.config = config;

		// Worlds load their own model file
		this.scoreTempModel = new ScoreTempModel(config.getWorldId(), config.getSensitivity());
		this.contributionModel = new ContributionModel(config.getWorldId());
		this.reductionModel = new ReductionModel(config.getWorldId());
		this.entityFitnessModel = new EntityFitnessModel(config.getWorldId());
		this.carbonIndexModel = new CarbonIndexModel(config.getWorldId());
	}

	public Reduction treeGrow(Tree tree, TreeType treeType, List<BlockState> blocks) {
		int reductionValue = 0;
		for (BlockState bs : blocks) {
			reductionValue += reductionModel.getReduction(bs.getType());
		}

		Integer uniqueId = GlobalWarming.getInstance().getRandom().nextInt(Integer.MAX_VALUE);
		Reduction reduction = new Reduction();
		reduction.setUniqueID(uniqueId);
		reduction.setWorldId(config.getWorldId());
		reduction.setReductioner(tree.getOwnerId());
		reduction.setReductionKey(tree.getUniqueId());
		reduction.setReductionValue(reductionValue);
		return reduction;
	}

	public Contribution furnaceBurn(Furnace furnace, ItemStack fuel) {
		Contribution contribution = null;
		if (furnace == null) {
			GlobalWarming.getInstance().getLogger().severe("Furnace null");
		} else if (fuel == null) {
			GlobalWarming.getInstance().getLogger().severe("Fuel null");
		} else {
			Integer uniqueId = GlobalWarming.getInstance().getRandom().nextInt(Integer.MAX_VALUE);
			contribution = new Contribution();
			contribution.setUniqueID(uniqueId);
			contribution.setWorldId(config.getWorldId());
			contribution.setContributer(furnace.getOwnerId());
			contribution.setContributionKey(furnace.getUniqueId());
			contribution.setContributionValue((int) contributionModel.getContribution(fuel.getType()));
		}

		return contribution;
	}

	public double getTemperature() throws NullPointerException {
		WorldTable worldTable = GlobalWarming.getInstance().getTableManager().getWorldTable();
		GWorld gWorld = worldTable.getWorld(config.getWorldId());
		if (gWorld == null) {
			throw new NullPointerException(String.format("World not found: [%s]", config.getWorldId()));
		}

		return scoreTempModel.getTemperature(gWorld.getCarbonValue());
	}

	public boolean isEffectEnabled(ClimateEffectType type) {
		return isEnabled() && config.getEnabledEffects().contains(type);
	}

	public boolean isEnabled() {
		return this.config.isEnabled();
	}
}
