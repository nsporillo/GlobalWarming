package net.porillo.database.tables;

import lombok.Getter;
import net.porillo.GlobalWarming;
import net.porillo.database.api.SelectCallback;
import net.porillo.database.queries.delete.EntityDeleteQuery;
import net.porillo.database.queries.select.EntitySelectQuery;
import net.porillo.database.queries.update.EntityUpdateQuery;
import net.porillo.database.queue.AsyncDBQueue;
import net.porillo.objects.TrackedEntity;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class EntityTable extends Table implements SelectCallback<TrackedEntity> {

    /**
     * [ENTITY_ID] -> [ENTITY]
     */
    @Getter private Map<UUID, TrackedEntity> entityMap = new HashMap<>();

    /**
     * PLAYER_ID -> SET(ENTITY_ID)
     */
    @Getter private Map<Integer, HashSet<UUID>> playerMap = new HashMap<>();

    public EntityTable() {
        super("entities");
        createIfNotExists();

        EntitySelectQuery selectQuery = new EntitySelectQuery(this);
        AsyncDBQueue.getInstance().queueSelectQuery(selectQuery);

    }
    @Override
    public void onSelectionCompletion(List<TrackedEntity> returnList) {
        if (GlobalWarming.getInstance() != null) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    for (TrackedEntity entity : returnList) {
                        updateCollections(entity);
                    }
                }
            }.runTask(GlobalWarming.getInstance());
        } else {
            System.out.printf("Selection returned %d furnaces.%n", returnList.size());
        }
    }

    public TrackedEntity delete(UUID entityId, boolean purge) {
        TrackedEntity trackedEntity = this.deleteEntity(entityId);
        if (trackedEntity != null) {
            if (purge) {
                EntityDeleteQuery deleteQuery = new EntityDeleteQuery(trackedEntity);
                AsyncDBQueue.getInstance().queueDeleteQuery(deleteQuery);
            } else {
                EntityUpdateQuery updateQuery = new EntityUpdateQuery(trackedEntity);
                AsyncDBQueue.getInstance().queueUpdateQuery(updateQuery);
            }
        }

        return trackedEntity;
    }


    private TrackedEntity deleteEntity(UUID entityId) {
        TrackedEntity deletedEntity = null;

        if (entityMap.containsKey(entityId)) {
            deletedEntity = entityMap.remove(entityId);

            //PLAYER_ID -> SET(ENTITY_ID):
            if (deletedEntity != null && playerMap.containsKey(deletedEntity.getBreederId())) {
                playerMap.get(deletedEntity.getBreederId()).remove(entityId);
            }
        }

        return deletedEntity;
    }

    /**
     * Handles all storage for entities collections
     *
     * @param entity Tracked Entity
     */
    public void updateCollections(TrackedEntity entity) {
        //PLAYER_ID -> SET(ENTITY_ID):
        final int breederID = entity.getBreederId();

        if (!playerMap.containsKey(breederID)) {
            HashSet<UUID> idSet = new HashSet<>();
            idSet.add(entity.getUuid());
            playerMap.put(breederID, idSet);
        } else {
            HashSet<UUID> idSet = playerMap.get(breederID);
            idSet.add(entity.getUuid());
        }

        //[ENTITY_ID] -> [ENTITY]:
        entityMap.put(entity.getUuid(), entity);
    }
}
