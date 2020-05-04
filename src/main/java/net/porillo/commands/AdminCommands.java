package net.porillo.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.porillo.GlobalWarming;
import net.porillo.config.Lang;
import net.porillo.database.queries.update.WorldUpdateQuery;
import net.porillo.database.queue.AsyncDBQueue;
import net.porillo.database.tables.WorldTable;
import net.porillo.effect.EffectEngine;
import net.porillo.effect.api.ClimateEffectType;
import net.porillo.effect.negative.SeaLevelRise;
import net.porillo.engine.ClimateEngine;
import net.porillo.engine.api.WorldClimateEngine;
import net.porillo.objects.GPlayer;
import net.porillo.objects.GWorld;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("gw")
public class AdminCommands extends BaseCommand {

    @Subcommand("debug")
    @CommandPermission("globalwarming.admin.debug")
    public class DebugCommands extends BaseCommand {

        @Subcommand("db")
        @Description("Toggles database console logging")
        public void onDatabaseDebug(CommandSender sender, String[] args) {
            boolean value = AsyncDBQueue.getInstance().isDebug();
            AsyncDBQueue.getInstance().setDebug(!value);
            if (!value) {
                sender.sendMessage(String.format(
                        "%sDatabase console logging = %strue.",
                        ChatColor.GREEN,
                        ChatColor.YELLOW));
            } else {
                sender.sendMessage(String.format(
                        "%sDatabase console logging = %sfalse.",
                        ChatColor.GREEN,
                        ChatColor.GRAY));
            }
        }

        /**
         * Set the temperature to activate / deactivate climate-effects
         * including sea-level, farm yields, slowness and more
         */
        @Subcommand("temp")
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

                updateTemperature(gPlayer, temperature);
            } else {
                gPlayer.sendMsg(String.format("%sInvalid arguments", ChatColor.RED));
            }
        }

        /**
         * Use this to force the sea to reset (i.e., server-reloads will lose block-meta data)
         * - WARNING: affects *all* water above sea level to the max config height
         */
        @Subcommand("")
        @Description("After a reload, sea-meta-data is lost, this deletes loaded water above sea level to the max config height")
        @Syntax("[repair|resume]")
        public void onRepairSea(GPlayer gPlayer, String[] args) {
            if (args.length == 1) {
                if (!args[0].equalsIgnoreCase("repair") && !args[0].equalsIgnoreCase("resume")) {
                    gPlayer.sendMsg(String.format(
                            Lang.GENERIC_INVALIDARGS.get(),
                            "[repair|resume]"));
                } else {
                    boolean isOverride = args[0].equalsIgnoreCase("repair");
                    WorldClimateEngine climateEngine = ClimateEngine.getInstance().getClimateEngine(gPlayer.getWorldId());
                    if (climateEngine != null) {
                        SeaLevelRise seaLevelRise = EffectEngine.getInstance().getEffect(SeaLevelRise.class, ClimateEffectType.SEA_LEVEL_RISE);
                        seaLevelRise.setOverride(isOverride);
                        updateTemperature(gPlayer, 14.0);
                    } else {
                        gPlayer.sendMsg(String.format("%sInvalid arguments", ChatColor.RED));
                    }
                }
            }
        }

        private void updateTemperature(GPlayer gPlayer, double temperature) {
            GWorld gWorld = null;
            WorldClimateEngine climateEngine = null;
            Player onlinePlayer = gPlayer.getOnlinePlayer();
            boolean isTemperatureUpdated = false;
            if (onlinePlayer != null) {
                climateEngine = ClimateEngine.getInstance().getClimateEngine(gPlayer.getWorldId());
                for (int carbonScore : climateEngine.getScoreTempModel().getTemperatureMap().keySet()) {
                    if (climateEngine.getScoreTempModel().getTemperatureMap().get(carbonScore) == temperature) {
                        WorldTable worldTable = GlobalWarming.getInstance().getTableManager().getWorldTable();
                        gWorld = worldTable.getWorld(gPlayer.getWorldId());
                        gWorld.setCarbonValue(carbonScore);
                        isTemperatureUpdated = true;
                        break;
                    }
                }
            }

            if (isTemperatureUpdated) {
                //Database update:
                WorldUpdateQuery updateQuery = new WorldUpdateQuery(gWorld);
                AsyncDBQueue.getInstance().queueUpdateQuery(updateQuery);

                //Notify:
                GlobalWarming.getInstance().getScoreboard().update(gPlayer);
                gPlayer.sendMsg(
                        String.format("World carbon score: [%s], temperature: [%s]",
                                gWorld.getCarbonValue(),
                                climateEngine.getTemperature()));
            } else {
                gPlayer.sendMsg("Temperature was not updated");
            }
        }
    }
}
