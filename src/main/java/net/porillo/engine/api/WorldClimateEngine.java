package net.porillo.engine.api;

import com.google.gson.Gson;
import lombok.Getter;
import net.porillo.GlobalWarming;
import net.porillo.config.Lang;
import net.porillo.config.WorldConfig;
import net.porillo.database.tables.WorldTable;
import net.porillo.effect.api.ClimateEffectType;
import net.porillo.engine.models.*;
import net.porillo.objects.*;
import net.porillo.util.Colorizer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public class WorldClimateEngine {

    private WorldConfig config;

    private ScoreTempModel scoreTempModel;
    private FuelModel fuelModel;
    private EntityMethaneModel methaneModel;
    private ReductionModel reductionModel;
    private EntityFitnessModel entityFitnessModel;
    private CarbonIndexModel carbonIndexModel;
    private DecimalFormat format;

    public WorldClimateEngine(WorldConfig config) {
        this.config = config;

        // Worlds load their own model file
        World world = Bukkit.getWorld(config.getWorldId());

        if (world != null) {
            Gson gson = GlobalWarming.getInstance().getGson();
            String worldName = Bukkit.getWorld(config.getWorldId()).getName();
            this.scoreTempModel = new ScoreTempModel(worldName, config.getSensitivity());
            this.fuelModel = new FuelModel(gson, worldName);
            this.methaneModel = new EntityMethaneModel(worldName);
            this.reductionModel = new ReductionModel(gson, worldName);
            this.entityFitnessModel = new EntityFitnessModel(worldName);
            this.carbonIndexModel = new CarbonIndexModel(worldName);
            String format = GlobalWarming.getInstance().getConf().getTemperatureFormat();
            this.format = new DecimalFormat(format);
        } else {
            GlobalWarming.getInstance().getLogger().warning(
                    String.format("Could not load climate engine for world id [%s]", config.getWorldId()));
        }

    }

    public Reduction treeGrow(Tree tree, List<BlockState> blocks, boolean bonemealUsed) {
        // Remove duplicate block states by collecting them into a hashmap with location as the key
        Map<Location, Material> blockStateMap = blocks.stream()
                .collect(Collectors.toMap(BlockState::getLocation, BlockState::getType, (a, b) -> b));

        double reductionValue = 0;
        int numBlocks = blockStateMap.size();

        for (Material material : blockStateMap.values()) {
            double reduction = reductionModel.getReduction(material);
            reductionValue += reduction;
        }

        if (bonemealUsed && !config.isBonemealReductionAllowed()) {
            return null;
        } else if (bonemealUsed) {
            reductionValue = (reductionValue * config.getBonemealReductionModifier());
        }

        Integer uniqueId = GlobalWarming.getInstance().getRandom().nextInt(Integer.MAX_VALUE);
        Reduction reduction = new Reduction();
        reduction.setUniqueID(uniqueId);
        reduction.setWorldId(config.getWorldId());
        reduction.setReductioner(tree.getOwnerId());
        reduction.setReductionKey(tree.getUniqueId());
        reduction.setReductionValue((int) reductionValue);
        reduction.setNumBlocks(numBlocks);
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
            double contribValue = fuelModel.getContribution(fuel.getType());
            contribution.setContributionValue((int) contribValue);
        }

        return contribution;
    }

    public Contribution methaneRelease(TrackedEntity entity) {
        Contribution contribution = null;
        if (entity == null) {
            GlobalWarming.getInstance().getLogger().severe("Entity null!");
        } else {
            Integer uniqueId = GlobalWarming.getInstance().getRandom().nextInt(Integer.MAX_VALUE);
            contribution = new Contribution();
            contribution.setUniqueID(uniqueId);
            contribution.setWorldId(config.getWorldId());
            contribution.setContributer(entity.getBreederId());
            contribution.setContributionKey(entity.getUniqueId());

            double contribValue = methaneModel.getContribution(entity.getEntityType());
            double modifier = config.getMethaneTicksLivedModifier();
            contribValue += (int) (entity.getTicksLived() / 20 / 60 * modifier);
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

    public String formatTemp(double temp) {
        return String.format(
                Lang.SCORE_TEMPERATURE.get(),
                Colorizer.getTemperatureColor(temp),
                format.format(temp));
    }

    public boolean isEffectEnabled(ClimateEffectType type) {
        return isEnabled() && config.getEnabledEffects().contains(type);
    }

    public boolean isEnabled() {
        return this.config.isEnabled();
    }
}
