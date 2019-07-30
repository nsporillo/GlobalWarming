package net.porillo.util;

import lombok.Getter;
import net.porillo.GlobalWarming;
import net.porillo.commands.GeneralCommands;
import net.porillo.config.Lang;
import net.porillo.database.tables.PlayerTable;
import net.porillo.engine.ClimateEngine;
import net.porillo.engine.api.WorldClimateEngine;
import net.porillo.objects.GPlayer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Maintain one scoreboard per associated-world (assuming there can be more than one)
 * - Scoreboards show the world's temperature and a list of local players
 * - Players request score updates when their carbon scores change
 * - Duplicate requests are ignored (one request per player)
 * - Scoreboard updates happen only when requests are available, clearing the queue
 * <p>
 * Notes:
 * - Player's scores are tied to their associated-world, not the current one
 * - Only one objective can be displayed in a sidebar at one time
 */
public class GScoreboard {
    @Getter private Map<UUID, Scoreboard> scoreboards;
    private ConcurrentLinkedQueue<UUID> requestQueue;
    private static final String GLOBAL_WARMING = "GlobalWarming";
    private static final long SCOREBOARD_INTERVAL_TICKS = GlobalWarming.getInstance().getConf().getScoreboardInterval();

    public GScoreboard() {
        //One scoreboard per world:
        scoreboards = new HashMap<>();

        //Queue of players requesting a score update:
        requestQueue = new ConcurrentLinkedQueue<>();

        //Watch for player updates (all worlds):
        debounceScoreUpdates();
    }

    /**
     * Player's use their associated-world's scoreboard
     * - The player may be in a different world, that's ok
     * - Creates the scoreboard if not found
     */
    private Scoreboard getScoreboard(GPlayer gPlayer) {
        return getScoreboard(gPlayer.getAssociatedWorldId(), true);
    }

    /**
     * Get any scoreboard by world
     * - Note: players should use their associated-world (not current-world)
     */
    private Scoreboard getScoreboard(UUID worldId, boolean isCreateIfNotFound) {
        Scoreboard scoreboard = null;
        if (ClimateEngine.getInstance().isClimateEngineEnabled(worldId)) {
            if (scoreboards.containsKey(worldId)) {
                //Existing scoreboard:
                scoreboard = scoreboards.get(worldId);
            } else if (isCreateIfNotFound) {
                //New scoreboard:
                scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
                scoreboards.put(worldId, scoreboard);

                //Objective (scoreboard title / group):
                Objective objective = scoreboard.registerNewObjective(
                        GLOBAL_WARMING,
                        "scores",
                        "Carbon Score");

                objective.setDisplaySlot(DisplaySlot.SIDEBAR);
            }
        }

        return scoreboard;
    }

    /**
     * Connect the player to the scoreboard
     * - Disconnects from any existing scoreboards
     * - Creates a new scoreboard for the world if required
     */
    public void connect(GPlayer gPlayer) {
        if (gPlayer != null) {
            //Disconnect the player from the current scoreboard (if required):
            disconnect(gPlayer);

            //Connect online players to their associated-world scoreboards:
            Player onlinePlayer = gPlayer.getOnlinePlayer();
            if (onlinePlayer != null) {
                Scoreboard scoreboard = getScoreboard(gPlayer);
                onlinePlayer.setScoreboard(scoreboard);
                Team team = scoreboard.registerNewTeam(onlinePlayer.getName());
                team.addEntry(onlinePlayer.getName());
                update(gPlayer);
            }
        }
    }

    /**
     * Disconnect the player from the scoreboard
     * - Removes the player from their team (i.e., player-color)
     * - Removes their score from the scoreboard
     * - The scoreboard will still be displayed on the player's client
     * until a new scoreboard is assigned or the user exits
     */
    public void disconnect(GPlayer gPlayer) {
        UUID associatedWorldId = gPlayer.getAssociatedWorldId();
        Scoreboard scoreboard = getScoreboard(associatedWorldId, false);
        if (scoreboard != null) {
            //Remove the team (i.e., player-color)
            OfflinePlayer player = Bukkit.getOfflinePlayer(gPlayer.getUuid());
            Team team = scoreboard.getTeam(player.getName());
            if (team != null) {
                team.removeEntry(player.getName());
                team.unregister();
            }

            //Remove the player's score:
            scoreboard.resetScores(player.getName());

            //Delete unused scoreboards:
            if (scoreboard.getEntries().size() == 0) {
                scoreboards.remove(associatedWorldId);
            }
        }
    }

    /**
     * Request a score update
     * - One unique request per player only
     */
    public void update(GPlayer player) {
        synchronized (this) {
            if (player != null) {
                if (!requestQueue.contains(player.getUuid())) {
                    requestQueue.add(player.getUuid());
                }
            }
        }
    }

    /**
     * Show or hide the scoreboard (UI)
     */
    public void show(GPlayer gPlayer, boolean isVisible) {
        Scoreboard scoreboard = getScoreboard(gPlayer);
        if (isVisible) {
            Objective objective = scoreboard.getObjective(GLOBAL_WARMING);
            if (objective != null) {
                objective.setDisplaySlot(DisplaySlot.SIDEBAR);
            }
        } else {
            scoreboard.clearSlot(DisplaySlot.SIDEBAR);
        }
    }

    /**
     * Update the global score for all worlds
     */
    private void updateGlobalScores() {
        for (World world : Bukkit.getWorlds()) {
            //Do not update worlds with disabled climate-engines:
            WorldClimateEngine climateEngine = ClimateEngine.getInstance().getClimateEngine(world.getUID());
            if (climateEngine != null && climateEngine.isEnabled()) {
                //Get the scoreboard for this world:
                Scoreboard scoreboard = getScoreboard(world.getUID(), false);

                //Get its objective (scoreboard title / group):
                Objective objective = null;
                if (scoreboard != null) {
                    objective = scoreboard.getObjective(GLOBAL_WARMING);
                }

                //Update the title to show this world's temperature:
                if (objective != null) {
                    double temperature = climateEngine.getTemperature();
                    String format = GlobalWarming.getInstance().getConf().getTemperatureFormat();
                    DecimalFormat decimalFormat = new DecimalFormat(format);
                    objective.setDisplayName(String.format(
                            Lang.SCORE_TEMPERATURE.get(),
                            GeneralCommands.getTemperatureColor(temperature),
                            decimalFormat.format(temperature)));
                }
            }
        }
    }

    /**
     * Update player-scores based on requests
     * - Processes all requests in the queue
     */
    private void updatePlayerScores(Queue<UUID> players) {
        for (UUID uuid : players) {
            PlayerTable playerTable = GlobalWarming.getInstance().getTableManager().getPlayerTable();
            GPlayer gPlayer = playerTable.getPlayers().get(uuid);
            if (gPlayer != null) {
                updatePlayerScore(gPlayer);
            }
        }
    }

    /**
     * Display a player's score
     * - Uses the player's associated-world scoreboard
     * - Note: the player may be in a different world, that's ok
     * - Player names are mapped to color warmth (based on their score)
     */
    private void updatePlayerScore(GPlayer gPlayer) {
        if (gPlayer != null) {
            //Do not update associated-worlds with disabled climate-engines:
            // - Ignore offline players (e.g., someone completing an offline player's bounty)
            Player onlinePlayer = gPlayer.getOnlinePlayer();
            if (onlinePlayer != null && ClimateEngine.getInstance().isClimateEngineEnabled(gPlayer.getAssociatedWorldId())) {
                //Update the player's score:
                Scoreboard scoreboard = getScoreboard(gPlayer);
                if (scoreboard != null) {
                    Objective objective = scoreboard.getObjective(GLOBAL_WARMING);
                    if (objective != null) {
                        Team team = scoreboard.getPlayerTeam(onlinePlayer);
                        if (team != null) {
                            team.setColor(GeneralCommands.getScoreColor(gPlayer.getCarbonScore()));
                            objective.getScore(onlinePlayer).setScore(gPlayer.getCarbonScore());
                        }
                    }
                }
            }
        }
    }

    /**
     * Update the scoreboard if requests are available
     * - Updates are processed periodically
     */
    private void debounceScoreUpdates() {
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(
                GlobalWarming.getInstance(),
                () -> {
                    //Make a copy of the update and clear the old update:
                    // - Synchronized to temporarily prevent threaded additions
                    Queue<UUID> players = null;
                    synchronized (this) {
                        if (!requestQueue.isEmpty()) {
                            players = new ConcurrentLinkedQueue<>(requestQueue);
                            requestQueue.clear();
                        }
                    }

                    //Process scores for any players in the update:
                    if (players != null && !players.isEmpty()) {
                        updateGlobalScores();
                        updatePlayerScores(players);
                    }
                }, 0L, SCOREBOARD_INTERVAL_TICKS);
    }
}
