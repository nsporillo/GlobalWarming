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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorldClimateEngine {

	private final String worldName;

	@Getter private WorldConfig config;

	// Models
	@Getter
	private Map<Model.ModelType, Model> models;

	public WorldClimateEngine(WorldConfig config) {
		this.worldName = config.getWorld();
		this.config = config;
		// Worlds load their own model file
		models = new HashMap<>();
		models.put(Model.ModelType.SCORE_TEMP, new ScoreTempModel(worldName));
		models.put(Model.ModelType.CONTRIBUTION, new ContributionModel(worldName));
		models.put(Model.ModelType.REDUCTION, new ReductionModel(worldName));
		models.put(Model.ModelType.ENTITY_FITNESS, new EntityFitnessModel(worldName));
		models.put(Model.ModelType.CARBON_INDEX, new CarbonIndexModel(worldName));
	}

	public <T extends Model> T getModel(Class<T> clazz, Model.ModelType modelType) {
		return clazz.cast(models.get(modelType));
	}

	public Reduction treeGrow(Tree tree, TreeType treeType, List<BlockState> blocks) {
		int reductionValue = 0;

		for (BlockState bs : blocks) {
			reductionValue += getModel(ReductionModel.class, Model.ModelType.REDUCTION).getReduction(bs.getType());
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
		contribution.setContributionValue((int) getModel(ContributionModel.class, Model.ModelType.CONTRIBUTION).getContribution(fuel.getType()));
		return contribution;
	}

	public Double getTemperature() {
		WorldTable worldTable = GlobalWarming.getInstance().getTableManager().getWorldTable();
		return getModel(ScoreTempModel.class, Model.ModelType.SCORE_TEMP).getTemperature(worldTable.getWorld(worldName).getCarbonValue());
	}

	public boolean isEffectEnabled(ClimateEffectType type) {
		return isEnabled() && config.getWorld().equals(worldName) && config.getEnabledEffects().contains(type);
	}

	public boolean isEnabled() {
		return this.config.isEnabled();
	}
}
