package net.porillo.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.porillo.GlobalWarming;
import net.porillo.database.queries.update.WorldUpdateQuery;
import net.porillo.database.queue.AsyncDBQueue;
import net.porillo.database.tables.WorldTable;
import net.porillo.effect.EffectEngine;
import net.porillo.effect.api.ClimateEffectType;
import net.porillo.effect.api.change.block.BlockChange;
import net.porillo.effect.api.change.block.SyncChunkUpdateTask;
import net.porillo.effect.negative.SeaLevelRise;
import net.porillo.engine.ClimateEngine;
import net.porillo.engine.api.WorldClimateEngine;
import net.porillo.objects.GPlayer;
import net.porillo.objects.GWorld;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@CommandAlias("globalwarming|gw")
public class AdminCommands extends BaseCommand {

    @Subcommand("debug")
    @CommandPermission("globalwarming.admin.debug")
    public class DebugCommands extends BaseCommand {

        @Subcommand("database|db")
        @Description("Toggles database console logging")
        public void onDatabaseDebug(GPlayer gPlayer, String[] args) {
            boolean value = AsyncDBQueue.getInstance().isDebug();
            AsyncDBQueue.getInstance().setDebug(!value);
            if (!value) {
                gPlayer.sendMsg(String.format("%sDatabase console logging = %strue.", ChatColor.GREEN, ChatColor.YELLOW));
            } else {
                gPlayer.sendMsg(String.format("%sDatabase console logging = %sfalse.", ChatColor.GREEN, ChatColor.GRAY));
            }
        }

        @Subcommand("temperature")
        @Description("Set the temperature for the current world")
        @Syntax("[celsius]")
        public void onTemperature(GPlayer gPlayer, String[] args) {
            if (args.length == 1) {
                double temperature;
                try {
                    temperature = Math.floor(Double.parseDouble(args[0]));
                } catch (NumberFormatException e) {
                    gPlayer.sendMsg(String.format("%sInvalid temperature", ChatColor.RED));
                    return;
                }

                GWorld gWorld = null;
                Player onlinePlayer = gPlayer.getOnlinePlayer();
                if (onlinePlayer != null) {
                    WorldClimateEngine climateEngine = ClimateEngine.getInstance().getClimateEngine(gPlayer.getWorldId());
                    for (int carbonScore : climateEngine.getScoreTempModel().getTemperatureMap().keySet()) {
                        if (climateEngine.getScoreTempModel().getTemperatureMap().get(carbonScore) == temperature) {
                            WorldTable worldTable = GlobalWarming.getInstance().getTableManager().getWorldTable();
                            gWorld = worldTable.getWorld(gPlayer.getWorldId());
                            gWorld.setCarbonValue(carbonScore);
                            gWorld.setTemperature(temperature);
                            break;
                        }
                    }
                }

                if (gWorld != null) {
                    //Database update:
                    WorldUpdateQuery updateQuery = new WorldUpdateQuery(gWorld);
                    AsyncDBQueue.getInstance().queueUpdateQuery(updateQuery);

                    //Notify:
                    GlobalWarming.getInstance().getScoreboard().update(gPlayer);
                    gPlayer.sendMsg(
                          String.format("World carbon score: [%s], temperature: [%s]",
                                gWorld.getCarbonValue(),
                                gWorld.getTemperature()));
                } else {
                    gPlayer.sendMsg("Temperature was not updated");
                }
            } else {
                gPlayer.sendMsg(String.format("%sInvalid arguments", ChatColor.RED));
            }
        }

        @Subcommand("effect")
        @CommandPermission("globalwarming.admin.debug.effect")
        public class EffectCommands extends BaseCommand {

            @Subcommand("sealevel")
            @Syntax("[level]")
            @Description("Apply the Sea Level effect to the current block")
            public void onSeaLevel(GPlayer gPlayer, String[] args) {
                if (args.length == 1) {
                    int seaLevelDelta;
                    try {
                        seaLevelDelta = Integer.parseInt(args[0]);
                    } catch (NumberFormatException e) {
                        gPlayer.sendMsg(String.format("%sInvalid sea level rise", ChatColor.RED));
                        return;
                    }

                    Player onlinePlayer = gPlayer.getOnlinePlayer();
                    if (onlinePlayer != null) {
                        //Get a list of changes:
                        Supplier<HashSet<BlockChange>> changes = EffectEngine.getInstance().getEffect(
                              SeaLevelRise.class,
                              ClimateEffectType.SEA_LEVEL_RISE).execute(onlinePlayer.getLocation().getChunk().getChunkSnapshot(),
                              seaLevelDelta);

                        //Schedule the changes:
                        new SyncChunkUpdateTask(onlinePlayer.getLocation().getChunk(), CompletableFuture.supplyAsync(changes)).runTaskLater(GlobalWarming.getInstance(), 40L);

                        //Notify:
                        gPlayer.sendMsg(String.format("%s%s", ChatColor.GREEN, String.format(
                              "Sea level rise scheduled from: [%s] by: [%s] blocks at chunk: [%s]",
                              onlinePlayer.getLocation().getWorld().getSeaLevel(),
                              seaLevelDelta,
                              onlinePlayer.getLocation().getChunk().toString())));
                    }
                } else {
                    gPlayer.sendMsg(String.format("%sInvalid arguments", ChatColor.RED));
                }
            }
        }
    }
}

