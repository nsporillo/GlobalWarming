package net.porillo.database.tables;

import lombok.Getter;
import net.porillo.GlobalWarming;
import net.porillo.database.api.SelectCallback;
import net.porillo.database.queries.insert.PlayerInsertQuery;
import net.porillo.database.queries.select.PlayerSelectQuery;
import net.porillo.database.queue.AsyncDBQueue;
import net.porillo.objects.GPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PlayerTable extends Table implements SelectCallback<GPlayer> {

    @Getter private Map<UUID, GPlayer> players = new HashMap<>();
    @Getter private Map<Integer, UUID> uuidMap = new HashMap<>();

    public PlayerTable() {
        super("players");
        createIfNotExists();

        PlayerSelectQuery selectQuery = new PlayerSelectQuery(this);
        AsyncDBQueue.getInstance().queueSelectQuery(selectQuery);
    }

    public GPlayer getOrCreatePlayer(UUID uuid) {
        GPlayer gPlayer;
        if (players.containsKey(uuid)) {
            //Existing players:
            gPlayer = players.get(uuid);
        } else {
            //New players:
            // - Get the player's world when known
            // - Note: the player record is NULL when using the untracked UUID
            UUID worldId = null;
            Player onlinePlayer = Bukkit.getPlayer(uuid);
            if (onlinePlayer != null) {
                worldId = onlinePlayer.getWorld().getUID();
            }

            //Or use the default world:
            if (worldId == null) {
                worldId = Bukkit.getWorlds().get(0).getUID();
            }

            //GW identifier:
            Integer uniqueId =
                  onlinePlayer == null
                        ? 0
                        : GlobalWarming.getInstance().getRandom().nextInt(Integer.MAX_VALUE);

            //New player:
            gPlayer = new GPlayer(
                  uniqueId,
                  uuid,
                  System.currentTimeMillis(),
                  0,
                  worldId);

            //Local storage:
            players.put(uuid, gPlayer);
            uuidMap.put(uniqueId, uuid);

            //Database update:
            PlayerInsertQuery insertQuery = new PlayerInsertQuery(gPlayer);
            AsyncDBQueue.getInstance().queueInsertQuery(insertQuery);
        }

        return gPlayer;
    }

    @Override
    public void onSelectionCompletion(List<GPlayer> returnList) throws SQLException {
        if (GlobalWarming.getInstance() != null) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    GlobalWarming.getInstance().getLogger().info(String.format("Loading %d players...", returnList.size()));
                    for (GPlayer gPlayer : returnList) {
                        if (!uuidMap.containsKey(gPlayer.getUniqueId())) {
                            uuidMap.put(gPlayer.getUniqueId(), gPlayer.getUuid());
                        }

                        if (!players.containsKey(gPlayer.getUuid())) {
                            players.put(gPlayer.getUuid(), gPlayer);
                        }
                    }
                }
            }.runTask(GlobalWarming.getInstance());
        } else {
            System.out.printf("Selection returned %d players.%n", returnList.size());
        }
    }
}
