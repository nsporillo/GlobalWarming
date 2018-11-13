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
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Maintain one scoreboard per associated-world (assuming there can be more than one)
 *  - Scoreboards show the world's temperature and a list of local players
 *  - Players request score updates when their carbon scores change
 *  - Duplicate requests are ignored (one request per player)
 *  - Scoreboard updates happen only when requests are available, clearing the queue
 *  - These updates happen within a second of the request (20 ticks)
 *
 * Notes:
 *  - Player's scores are tied to their associated-world, not the current one
 *  - Only one objective can be displayed in a sidebar at one time
 */
public class GScoreboard {
    @Getter
    private Map<String, Scoreboard> scoreboards;
    private ConcurrentLinkedQueue<UUID> requestQueue;
    private static final String GLOBAL_WARMING = "GlobalWarming";
    private static final long SCOREBOARD_INTERVAL_TICKS = 20;

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
    private Scoreboard getScoreboard(Player player) {
        String associatedWorldName = ClimateEngine.getInstance().getAssociatedWorldName(player);
        return getScoreboard(associatedWorldName, true);
    }

    /**
     * Get any scoreboard by world
     * - Note: players should use their associated-world (not current-world)
     */
    private Scoreboard getScoreboard(String worldName, boolean isCreateIfNotFound) {
        Scoreboard scoreboard = null;
        if (ClimateEngine.getInstance().isClimateEngineEnabled(worldName)) {
            if (scoreboards.containsKey(worldName)) {
                //Existing scoreboard:
                scoreboard = scoreboards.get(worldName);
            } else if (isCreateIfNotFound) {
                //New scoreboard:
                scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
                scoreboards.put(worldName, scoreboard);

                //Objective (scoreboard title / group):
                Objective objective = scoreboard.registerNewObjective(
                      GLOBAL_WARMING,
                      "dummy",
                      "[TITLE]");

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
        Player player = null;
        if (gPlayer != null) {
            player = gPlayer.getPlayer();
        }

        if (player != null) {
            //Disconnect from the current scoreboard (if required):
            disconnect(player);

            //Connect to the player's associated-world scoreboard:
            Scoreboard scoreboard = getScoreboard(player);
            player.setScoreboard(scoreboard);
            Team team = scoreboard.registerNewTeam(player.getName());
            team.addPlayer(player);
            update(gPlayer.getUuid());
        }
    }

    /**
     * Disconnect the player from the scoreboard
     * - Removes the player from their team (i.e., player-color)
     * - Removes their score from the scoreboard
     * - The scoreboard will still be displayed on the player's client
     *   until a new scoreboard is assigned or the user exits
     */
    public void disconnect(Player player) {
        String associatedWorldName = ClimateEngine.getInstance().getAssociatedWorldName(player);
        Scoreboard scoreboard = getScoreboard(associatedWorldName, false);
        if (scoreboard != null) {
            //Remove the team (i.e., player-color)
            Team team = scoreboard.getPlayerTeam(player);
            if (team != null) {
                team.removePlayer(player);
                team.unregister();
            }

            //Remove the player's score:
            scoreboard.resetScores(player);

            //Delete unused scoreboards:
            if (scoreboard.getPlayers().size() == 0) {
                scoreboards.remove(associatedWorldName);
            }
        }
    }


    /**
     * Request a score update
     * - One unique request per player only
     */
    public void update(UUID playerId) {
        synchronized (this) {
            if (!requestQueue.contains(playerId)) {
                requestQueue.add(playerId);
            }
        }
    }

    /**
     * Show or hide the scoreboard (UI)
     */
    public void show(Player player, boolean isVisible) {
        Scoreboard scoreboard = getScoreboard(player);
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
            WorldClimateEngine climateEngine = ClimateEngine.getInstance().getClimateEngine(world.getName());
            if (climateEngine != null && climateEngine.isEnabled()) {
                //Get the scoreboard for this world:
                String worldName = world.getName();
                Scoreboard scoreboard = getScoreboard(worldName, false);

                //Get its objective (scoreboard title / group):
                Objective objective = null;
                if (scoreboard != null) {
                    objective = scoreboard.getObjective(GLOBAL_WARMING);
                }

                //Update the title to show this world's temperature:
                if (objective != null) {
                    double temperature = climateEngine.getTemperature();
                    DecimalFormat decimalFormat = new DecimalFormat("#.##");
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
            Player player = gPlayer.getPlayer();
            if (ClimateEngine.getInstance().isAssociatedEngineEnabled(player)) {
                //Update the player's score:
                Scoreboard scoreboard = getScoreboard(player);
                if (scoreboard != null) {
                    Objective objective = scoreboard.getObjective(GLOBAL_WARMING);
                    if (objective != null) {
                        Team team = scoreboard.getPlayerTeam(player);
                        if (team != null) {
                            team.setColor(GeneralCommands.getScoreColor(gPlayer.getCarbonScore()));
                            objective.getScore(player).setScore(gPlayer.getCarbonScore());
                        }
                    }
                }
            }
        }
    }

    /**
     * Update the scoreboard if requests are available
     * - Updates are processed within a second (20 ticks)
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