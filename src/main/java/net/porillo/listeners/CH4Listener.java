package net.porillo.listeners;

import net.porillo.GlobalWarming;
import net.porillo.config.Lang;
import net.porillo.database.queries.insert.ContributionInsertQuery;
import net.porillo.database.queries.insert.EntityInsertQuery;
import net.porillo.database.queries.update.PlayerUpdateQuery;
import net.porillo.database.queue.AsyncDBQueue;
import net.porillo.database.tables.EntityTable;
import net.porillo.database.tables.PlayerTable;
import net.porillo.engine.ClimateEngine;
import net.porillo.engine.api.WorldClimateEngine;
import net.porillo.objects.Contribution;
import net.porillo.objects.GPlayer;
import net.porillo.objects.TrackedEntity;
import net.porillo.util.AlertManager;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.UUID;

public class CH4Listener implements Listener {

    private GlobalWarming gw;

    public CH4Listener(GlobalWarming gw) {
        this.gw = gw;
    }

    @EventHandler
    public void onEntityBreed(EntityBreedEvent event) {
        //Ignore if the entities world-climate is disabled:
        UUID worldId = event.getMother().getWorld().getUID();
        WorldClimateEngine eventClimateEngine = ClimateEngine.getInstance().getClimateEngine(worldId);
        if (eventClimateEngine == null || !eventClimateEngine.isEnabled()) {
            return;
        }

        PlayerTable playerTable = gw.getTableManager().getPlayerTable();
        LivingEntity breeder = event.getBreeder();

        if (breeder instanceof Player) {
            Player player = (Player) breeder;
            GPlayer gPlayer = playerTable.getOrCreatePlayer(player.getUniqueId());
            // Track mother, father, and child. Associate the player breeder to them.
            trackEntity(event.getMother(), gPlayer);
            trackEntity(event.getFather(), gPlayer);
            trackEntity(event.getEntity(), gPlayer);
        }
    }

    private void trackEntity(LivingEntity entity, GPlayer gPlayer) {
        EntityTable entityTable = gw.getTableManager().getEntityTable();

        if (!entityTable.getEntityMap().containsKey(entity.getUniqueId())) {
            int uniqueId = GlobalWarming.getInstance().getRandom().nextInt(Integer.MAX_VALUE);
            TrackedEntity trackedEntity = new TrackedEntity();
            trackedEntity.setUniqueId(uniqueId);
            trackedEntity.setUuid(entity.getUniqueId());
            trackedEntity.setBreederId(gPlayer.getUniqueId());
            trackedEntity.setEntityType(entity.getType());
            trackedEntity.setTicksLived(entity.getTicksLived());
            trackedEntity.setAlive(!entity.isDead());

            entityTable.updateCollections(trackedEntity);

            EntityInsertQuery insertQuery = new EntityInsertQuery(trackedEntity);
            AsyncDBQueue.getInstance().queueInsertQuery(insertQuery);
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        //Ignore if the entities world-climate is disabled:
        UUID worldId = event.getEntity().getWorld().getUID();
        WorldClimateEngine eventClimateEngine = ClimateEngine.getInstance().getClimateEngine(worldId);
        if (eventClimateEngine == null || !eventClimateEngine.isEnabled()) {
            return;
        }

        EntityTable entityTable = gw.getTableManager().getEntityTable();

        if (entityTable.getEntityMap().containsKey(event.getEntity().getUniqueId())) {
            TrackedEntity entity = entityTable.getEntityMap().get(event.getEntity().getUniqueId());
            entity.setTicksLived(event.getEntity().getTicksLived());
            entity.setAlive(false);
            int breederID = entity.getBreederId();
            PlayerTable playerTable = gw.getTableManager().getPlayerTable();
            GPlayer polluter = playerTable.getOrCreatePlayer(playerTable.getUuidMap().get(breederID));

            int contributionValue = 0;
            Contribution contribution = eventClimateEngine.methaneRelease(entity);
            if (contribution != null) {
                //Queue an insert into the contributions table:
                ContributionInsertQuery insertQuery = new ContributionInsertQuery(contribution);
                AsyncDBQueue.getInstance().queueInsertQuery(insertQuery);
                contributionValue = contribution.getContributionValue();

                // Execute real time player notification if they're subscribed with /gw score alerts
                AlertManager.getInstance().alert(polluter,
                        String.format(Lang.ALERT_FARMCONTRIB.get(),
                                entity.getEntityType().name().toLowerCase(), contribution.getContributionValue()));
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
            gw.getTableManager().getWorldTable().updateWorldCarbonValue(worldId, contributionValue);

            //Update the scoreboard:
            gw.getScoreboard().update(polluter);
            // Untrack the dead entity
            entityTable.delete(event.getEntity().getUniqueId(), false);
        }
    }
}
