package net.porillo.engine.api;

import lombok.Getter;
import net.porillo.GlobalWarming;
import net.porillo.config.WorldConfig;
import net.porillo.database.tables.WorldTable;
import net.porillo.effect.api.ClimateEffectType;
import net.porillo.engine.models.*;
import net.porillo.objects.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
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
		String worldName = Bukkit.getWorld(config.getWorldId()).getName();
		this.scoreTempModel = new ScoreTempModel(worldName, config.getSensitivity());
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
		reduction.setWorldId(config.getWorldId());
		reduction.setReductioner(tree.getOwnerId());
		reduction.setReductionKey(tree.getUniqueId());
		reduction.setReductionValue(reductionValue);
		return reduction;
	}

	public Contribution furnaceBurn(Furnace furnace, Material furnaceType, ItemStack fuel) {
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
			double contribValue = contributionModel.getContribution(fuel.getType());
			if (furnaceType == Material.BLAST_FURNACE) {
				contribValue *= config.getBlastFurnaceMultiplier();
			}
			contribution.setContributionValue((int) contribValue);
		}

		return contribution;
	}

	public double getTemperature() {
		double temperature = 14.0;
		WorldTable worldTable = GlobalWarming.getInstance().getTableManager().getWorldTable();
		GWorld gWorld = worldTable.getWorld(config.getWorldId());
		if (gWorld == null) {
			Thread.dumpStack();
			GlobalWarming.getInstance().getLogger().severe(String.format(
				"World ID not found in GWorld table [%s]: [%s]",
				WorldConfig.getDisplayName(config.getWorldId()),
				config.getWorldId()));
		} else {
			temperature = scoreTempModel.getTemperature(gWorld.getCarbonValue());
		}

		return temperature;
	}

	public boolean isEffectEnabled(ClimateEffectType type) {
		return isEnabled() && config.getEnabledEffects().contains(type);
	}

	public boolean isEnabled() {
		return this.config.isEnabled();
	}
}
