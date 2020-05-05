package net.porillo.listeners;

import lombok.RequiredArgsConstructor;
import net.porillo.GlobalWarming;
import net.porillo.config.Lang;
import net.porillo.database.queries.insert.ContributionInsertQuery;
import net.porillo.database.queries.insert.FurnaceInsertQuery;
import net.porillo.database.queries.insert.ReductionInsertQuery;
import net.porillo.database.queries.insert.TreeInsertQuery;
import net.porillo.database.queries.update.PlayerUpdateQuery;
import net.porillo.database.queries.update.TreeUpdateQuery;
import net.porillo.database.queue.AsyncDBQueue;
import net.porillo.database.tables.FurnaceTable;
import net.porillo.database.tables.PlayerTable;
import net.porillo.database.tables.TreeTable;
import net.porillo.engine.ClimateEngine;
import net.porillo.engine.api.WorldClimateEngine;
import net.porillo.objects.*;
import net.porillo.util.AlertManager;
import net.porillo.util.FurnaceQueue;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.inventory.*;

import java.util.*;

import static org.bukkit.event.EventPriority.*;
import static org.bukkit.event.inventory.InventoryAction.*;

@RequiredArgsConstructor
public class CO2Listener implements Listener {

    private final GlobalWarming gw;
    private static final UUID untrackedUUID = UUID.fromString("1-1-1-1-1");

    private Map<Location, FurnaceQueue> furnaceMap = new HashMap<>();

    /**
     * Detect when CO2 is emitted via furnace
     *
     * @param event furnace burn
     */
    @EventHandler(ignoreCancelled = true)
    public void onFurnaceSmelt(FurnaceBurnEvent event) {
        //Ignore if the block's world-climate is disabled:
        UUID worldId = event.getBlock().getWorld().getUID();
        WorldClimateEngine eventClimateEngine = ClimateEngine.getInstance().getClimateEngine(worldId);
        if (eventClimateEngine == null || !eventClimateEngine.isEnabled() || event.isCancelled()) {
            return;
        }

        //Setup:
        ItemStack fuel = event.getFuel();
        Location location = event.getBlock().getLocation();
        Material furnaceType = event.getBlock().getType();
        FurnaceTable furnaceTable = GlobalWarming.getInstance().getTableManager().getFurnaceTable();
        PlayerTable playerTable = GlobalWarming.getInstance().getTableManager().getPlayerTable();
        FurnaceQueue furnaceQueue = furnaceMap.get(location);
        Furnace furnace = null;
        GPlayer polluter = null;
        UUID affectedWorldId = null;


        //Known furnaces:
        // - Use the player's associated world
        // - Supports offline players with active furnaces
        Integer furnaceId = furnaceTable.getLocationMap().get(location);
        if (furnaceId != null) {
            furnace = (Furnace) furnaceTable.getBlockMap().get(furnaceId);
            UUID uuid = playerTable.getUuidMap().get(furnace.getOwnerId());

            if (furnaceQueue != null) {
                System.out.println("Burning fuel using furnace queue for " + fuel.getType());
                polluter = playerTable.getPlayers().get(furnaceQueue.burnFuel(fuel));
            } else {
                System.out.println("Falling back to furnace placer");
                polluter = playerTable.getPlayers().get(uuid);
            }

            if (polluter != null) {
                affectedWorldId = polluter.getAssociatedWorldId();
            }
        }



        //Unknown furnaces:
        // - This might happen if a player has a redstone hopper setup that feeds untracked furnaces
        // - In this case, just consider it to be untracked emissions
        // - Get the existing untracked-player or create a new record otherwise
        // - Use the event's associated world (not the untracked player's world)
        // - NOTE: the untracked player is responsible for unknown furnaces from *all worlds*
        if (furnace == null) {
            polluter = playerTable.getOrCreatePlayer(untrackedUUID);
            affectedWorldId = eventClimateEngine.getConfig().getAssociatedWorldId();

            //Create a new furnace object:
            int uniqueId = GlobalWarming.getInstance().getRandom().nextInt(Integer.MAX_VALUE);
            furnace = new Furnace(uniqueId, polluter.getUniqueId(), location, true);

            //Update all furnace collections:
            furnaceTable.updateCollections(furnace);

            //Database update:
            FurnaceInsertQuery insertQuery = new FurnaceInsertQuery(furnace);
            AsyncDBQueue.getInstance().queueInsertQuery(insertQuery);

            //Notification:
            gw.getLogger().warning(String.format("[%s] burned as fuel in an untracked furnace!", fuel.getType().name()));
            gw.getLogger().warning(String.format("@ %s", location.toString()));
        }

        //Carbon updates:
        // - Record the contribution
        // - Update the associated world's carbon level
        // - Update the player's carbon score
        WorldClimateEngine affectedClimateEngine = ClimateEngine.getInstance().getClimateEngine(affectedWorldId);
        if (affectedClimateEngine != null && affectedClimateEngine.isEnabled()) {
            //Carbon contribution record:
            int contributionValue = 0;
            Contribution contribution = eventClimateEngine.furnaceBurn(furnace, furnaceType, fuel);
            if (contribution != null) {
                //Queue an insert into the contributions table:
                ContributionInsertQuery insertQuery = new ContributionInsertQuery(contribution);
                AsyncDBQueue.getInstance().queueInsertQuery(insertQuery);
                contributionValue = contribution.getContributionValue();

                // Execute real time player notification if they're subscribed with /gw score alerts
                AlertManager.getInstance().alert(polluter,
                        String.format(Lang.ALERT_BURNCONTRIB.get(), fuel.getType().name().toLowerCase(),
                                contribution.getContributionValue()));
            }

            //Polluter carbon scores:
            if (polluter != null) {
                //Increment the polluter's carbon score:
                int carbonScore = polluter.getCarbonScore();
                polluter.setCarbonScore(carbonScore + contributionValue);

                //Queue an update to the player table:
                PlayerUpdateQuery updateQuery = new PlayerUpdateQuery(polluter);
                AsyncDBQueue.getInstance().queueUpdateQuery(updateQuery);
            }

            //Update the affected world's carbon levels:
            GlobalWarming.getInstance().getTableManager().getWorldTable().updateWorldCarbonValue(affectedWorldId, contributionValue);

            //Update the scoreboard:
            gw.getScoreboard().update(polluter);
        }
    }

    /**
     * Detect when CO2 is absorbed via new tree
     * @param event structure grow event (tree grow)
     */
    @EventHandler(ignoreCancelled = true)
    public void onStructureGrow(StructureGrowEvent event) {
        //Ignore if the location's world-climate is disabled:
        UUID worldId = event.getLocation().getWorld().getUID();
        WorldClimateEngine eventClimateEngine = ClimateEngine.getInstance().getClimateEngine(worldId);
        if (eventClimateEngine == null || !eventClimateEngine.isEnabled() || event.isCancelled()) {
            return;
        }

        //Setup:
        Location location = event.getLocation();
        TreeTable treeTable = GlobalWarming.getInstance().getTableManager().getTreeTable();
        PlayerTable playerTable = GlobalWarming.getInstance().getTableManager().getPlayerTable();
        Tree tree = null;
        GPlayer planter = null;
        UUID affectedWorldId = null;

        //Known trees:
        // - Update the tree record
        // - Use the player's associated world
        // - Supports offline players with active saplings
        if (treeTable.getLocationMap().containsKey(location)) {
            //Tree update:
            tree = (Tree) treeTable.getBlockMap().get(treeTable.getLocationMap().get(location));
            tree.setSapling(false);
            tree.setSize(event.getBlocks().size());

            //Database update:
            TreeUpdateQuery treeUpdateQuery = new TreeUpdateQuery(tree);
            AsyncDBQueue.getInstance().queueUpdateQuery(treeUpdateQuery);

            //Affected world:
            UUID uuid = playerTable.getUuidMap().get(tree.getOwnerId());
            planter = playerTable.getPlayers().get(uuid);
            if (planter != null) {
                affectedWorldId = planter.getWorldId();
            }
        } else {
            planter = playerTable.getOrCreatePlayer(untrackedUUID);
            affectedWorldId = eventClimateEngine.getConfig().getAssociatedWorldId();

            //Create a new tree object:
            Integer uniqueId = GlobalWarming.getInstance().getRandom().nextInt(Integer.MAX_VALUE);
            tree = new Tree(uniqueId, planter.getUniqueId(), location, false, event.getBlocks().size());

            //Update all tree collections:
            treeTable.updateCollections(tree);

            //Database update:
            TreeInsertQuery insertQuery = new TreeInsertQuery(tree);
            AsyncDBQueue.getInstance().queueInsertQuery(insertQuery);

            //Notification:
            gw.getLogger().warning(String.format("Untracked growing structure: [%s]", event.getSpecies().name()));
            gw.getLogger().warning(String.format("@ %s", location.toString()));
        }

        //Carbon updates:
        // - Record the reduction
        // - Update the associated world's carbon level
        // - Update the player's carbon score
        WorldClimateEngine affectedClimateEngine = ClimateEngine.getInstance().getClimateEngine(affectedWorldId);
        if (affectedClimateEngine != null && affectedClimateEngine.isEnabled()) {
            //Carbon reduction record:
            Reduction reduction = eventClimateEngine.treeGrow(tree, event.getBlocks(), event.isFromBonemeal());
            if (reduction == null) {
                return;
            }
            //Queue an insert into the contributions table:
            ReductionInsertQuery insertQuery = new ReductionInsertQuery(reduction);
            AsyncDBQueue.getInstance().queueInsertQuery(insertQuery);
            int reductionValue = reduction.getReductionValue();

            //Carbon scores:
            // - When player's are bounty-hunting the affected player is the bounty-owner,
            //   not the tree-planter
            GPlayer affectedPlayer = planter;
            OffsetBounty updatedBounty = OffsetBounty.update(planter, event.getBlocks().size());
            if (updatedBounty != null) {
                UUID bountyCreator = playerTable.getUuidMap().get(updatedBounty.getCreatorId());
                affectedPlayer = playerTable.getPlayers().get(bountyCreator);
            }

            if (affectedPlayer != null) {
                //Increment the planter's carbon score:
                int carbonScore = affectedPlayer.getCarbonScore();
                affectedPlayer.setCarbonScore(carbonScore - reductionValue);

                //Queue an update to the player table:
                PlayerUpdateQuery updateQuery = new PlayerUpdateQuery(affectedPlayer);
                AsyncDBQueue.getInstance().queueUpdateQuery(updateQuery);
            }

            // Execute real time player notification if they're subscribed with /gw score alerts
            if (event.isFromBonemeal()) {
                AlertManager.getInstance().alert(planter,
                        String.format(Lang.ALERT_TREEREDUCEBONEMEAL.get(),
                                reduction.getNumBlocks(), reduction.getReductionValue()));
            } else {
                AlertManager.getInstance().alert(planter,
                        String.format(Lang.ALERT_TREEREDUCE.get(),
                                reduction.getNumBlocks(), reduction.getReductionValue()));
            }

            //Update the affected world's carbon levels:
            GlobalWarming.getInstance().getTableManager().getWorldTable().updateWorldCarbonValue(affectedWorldId, -reductionValue);

            //Update the scoreboard:
            gw.getScoreboard().update(affectedPlayer);
        }
    }

    private final static Set<InventoryAction> furnaceActions = new HashSet<>();
    static {
        furnaceActions.add(PICKUP_ONE);
        furnaceActions.add(PICKUP_SOME);
        furnaceActions.add(PICKUP_HALF);
        furnaceActions.add(PICKUP_ALL);
        furnaceActions.add(MOVE_TO_OTHER_INVENTORY);
        furnaceActions.add(HOTBAR_SWAP);
        furnaceActions.add(HOTBAR_MOVE_AND_READD);
        furnaceActions.add(SWAP_WITH_CURSOR);
        furnaceActions.add(PLACE_ONE);
        furnaceActions.add(PLACE_SOME);
        furnaceActions.add(PLACE_ALL);
    }

    @EventHandler(priority = MONITOR, ignoreCancelled = true)
    public void onInventoryClick(final InventoryClickEvent event) {
        int slot = event.getRawSlot();
        if (slot < 0) {
            return;
        }

        Inventory inventory = event.getInventory();
        Location containerLoc = inventory.getLocation();

        // Virtual inventory or something (enderchest?)
        if (containerLoc == null) {
            return;
        }

        // Store some info
        final Player player = (Player) event.getWhoClicked();

        // Ignore all item move events where players modify their own inventory
        if (inventory.getHolder() instanceof Player) {
            Player other = (Player) inventory.getHolder();

            if (other.equals(player)) {
                return;
            }
        }

        boolean isTopInv = slot < inventory.getSize();

        ItemStack heldItem = event.getCursor();
        ItemStack slotItem = event.getCurrentItem();

        // This happens when opening someone else's inventory, so don't bother tracking it
        if (slotItem == null) {
            return;
        }

        System.out.println(String.format("player=%s,action=%s, click=%s, slotType=%s, result=%s, heldItem=%s, slotItem=%s",
                player.getName(), event.getAction(), event.getClick(),
                event.getSlotType(), event.getResult(), heldItem, slotItem));
        if (furnaceActions.contains(event.getAction())) {
            Inventory clicked = event.getClickedInventory();
            if (clicked instanceof FurnaceInventory && inventory instanceof PlayerInventory) {
                System.out.println("Moving items from furnace to player inventory");
            } else if (clicked instanceof PlayerInventory && inventory instanceof FurnaceInventory) {
                System.out.println("Moving items from player to furnace inventory");
            }
        } else {
            System.out.println("Non furnace action: " + event.getAction());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryMoveItemEvent(InventoryMoveItemEvent event) {
        ItemStack itemStack = event.getItem();
        Material type = itemStack.getType();
        System.out.println(event.getItem());
        if (type.isFuel()) {
            Inventory initiator = event.getSource();
            Inventory destination = event.getDestination();
            System.out.println(initiator);
            System.out.println(destination);

            if (initiator instanceof PlayerInventory && destination instanceof FurnaceInventory) {
                // Player is moving fuel into a furnace
                System.out.println("Player moved fuel into a furnace");
                FurnaceInventory furnaceInventory = (FurnaceInventory) destination;
                PlayerInventory playerInventory = (PlayerInventory) initiator;

                Location furnaceLocation = furnaceInventory.getLocation();
                UUID playerId = getHolder(playerInventory);

                insertFuel(furnaceLocation, playerId, itemStack);
            } else if (initiator instanceof FurnaceInventory && destination instanceof PlayerInventory) {
                // Player is moving fuel out of a furnace
                System.out.println("Player moved fuel out of a furnace");
                PlayerInventory playerInventory = (PlayerInventory) destination;
                FurnaceInventory furnaceInventory = (FurnaceInventory) initiator;
                Location furnaceLocation = furnaceInventory.getLocation();
                UUID playerId = getHolder(playerInventory);

                removeFuel(furnaceLocation, playerId, itemStack);
            }
        }
    }

    private UUID getHolder(PlayerInventory playerInventory) {
        HumanEntity holder = playerInventory.getHolder();
        return holder != null ? holder.getUniqueId() : null;
    }

    public void insertFuel(Location location, UUID playerId, ItemStack itemStack) {
        if (location == null || playerId == null) {
            gw.getLogger().warning("Could not track fuel insertion");
            return;
        }

        FurnaceQueue furnaceQueue = furnaceMap.get(location);
        if (furnaceQueue == null) {
            furnaceQueue = new FurnaceQueue();
            furnaceMap.put(location, furnaceQueue);
        }

        furnaceQueue.insertFuel(playerId, itemStack);
    }

    public void removeFuel(Location location, UUID playerId, ItemStack itemStack) {
        if (location == null || playerId == null) {
            gw.getLogger().warning("Could not track fuel insertion");
            return;
        }

        FurnaceQueue furnaceQueue = furnaceMap.get(location);
        if (furnaceQueue == null) {
            furnaceQueue = new FurnaceQueue();
            furnaceMap.put(location, furnaceQueue);
        }

        furnaceQueue.removeFuel(playerId, itemStack);
    }
}
