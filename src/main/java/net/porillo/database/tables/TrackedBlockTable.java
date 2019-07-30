package net.porillo.database.tables;

import lombok.Getter;
import net.porillo.objects.TrackedBlock;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@Getter
public class TrackedBlockTable extends Table {
    /**
     * [TREE_ID | FURNACE_ID] -> [TREE | FURNACE]
     */
    private Map<Integer, TrackedBlock> blockMap = new HashMap<>();

    /**
     * LOCATION -> [TREE_ID | FURNACE_ID]
     */
    private Map<Location, Integer> locationMap = new HashMap<>();

    /**
     * PLAYER_ID -> SET(TREE_ID | FURNACE_ID)
     */
    private Map<Integer, HashSet<Integer>> playerMap = new HashMap<>();

    TrackedBlockTable(String tableName) {
        super(tableName);
    }

    /**
     * Handles all storage for tree / furnace collections
     *
     * @param block tree / furnace
     */
    public void updateCollections(TrackedBlock block) {
        //PLAYER_ID -> SET(TREE_ID | FURNACE_ID):
        final int ownerId = block.getOwnerId();
        if (playerMap.containsKey(ownerId)) {
            HashSet<Integer> idSet = playerMap.get(ownerId);
            idSet.add(block.getUniqueId());
        } else {
            HashSet<Integer> idSet = new HashSet<>();
            idSet.add(block.getUniqueId());
            playerMap.put(ownerId, idSet);
        }

        //[TREE_ID | FURNACE_ID] -> [TREE | FURNACE]:
        blockMap.put(block.getUniqueId(), block);

        //LOCATION -> [TREE_ID | FURNACE_ID]:
        locationMap.put(block.getLocation(), block.getUniqueId());
    }

    /**
     * Determine if there is a tracked-block at the given location
     * - If so, update all block collections
     */
    public TrackedBlock deleteLocation(Location location) {
        TrackedBlock deletedBlock = null;
        if (locationMap.containsKey(location)) {
            //LOCATION -> [TREE_ID | FURNACE_ID]:
            Integer blockId = locationMap.get(location);
            locationMap.remove(location);

            //[TREE_ID | FURNACE_ID] -> [TREE | FURNACE]:
            deletedBlock = blockMap.get(blockId);
            blockMap.remove(blockId);

            //PLAYER_ID -> SET(TREE_ID | FURNACE_ID):
            if (deletedBlock != null && playerMap.containsKey(deletedBlock.getOwnerId())) {
                playerMap.get(deletedBlock.getOwnerId()).remove(blockId);
            }
        }

        return deletedBlock;
    }
}
