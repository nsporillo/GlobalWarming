package net.porillo.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.porillo.GlobalWarming;
import net.porillo.config.GlobalWarmingConfig;
import net.porillo.config.Lang;
import net.porillo.database.queue.AsyncDBQueue;
import net.porillo.effect.EffectEngine;
import net.porillo.effect.api.ClimateEffectType;
import net.porillo.effect.api.change.block.BlockChange;
import net.porillo.effect.api.change.block.SyncChunkUpdateTask;
import net.porillo.effect.negative.SeaLevelRise;
import net.porillo.engine.ClimateEngine;
import net.porillo.engine.api.Model;
import net.porillo.engine.api.WorldClimateEngine;
import net.porillo.objects.GPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@CommandAlias("globalwarming|gw")
public class AdminCommands extends BaseCommand {

    @Subcommand("reload")
    @CommandPermission("globalwarming.admin.reload")
    public class ReloadCommands extends BaseCommand {

        @Subcommand("config")
        @CommandCompletion("@config")
        @Description("Reload all configs or a specific one")
        public void reloadConfig(GPlayer gPlayer, String[] args) {
            if (args.length == 0) {
                // Reload all Config
                Lang.init();
                GlobalWarming.getInstance().getConf().load();
                gPlayer.sendMsg(Lang.ADMIN_RELOAD_SUCCESS, "all configs");
            } else {
                String configName = args[0];
                switch (configName) {
                    case "lang":
                        Lang.init();
                        break;
                    case "config":
                        GlobalWarming.getInstance().getConf().load();
                        break;
                    default:
                        gPlayer.sendMsg(Lang.ADMIN_RELOAD_INVALID_CONFIG);
                        return;
                }
                gPlayer.sendMsg(Lang.ADMIN_RELOAD_SUCCESS, configName);
            }
        }

        @Subcommand("model")
        @CommandCompletion("@model")
        @Description("Reload all models or a specific one")
        public void reloadModel (GPlayer gPlayer, String[] args){
            WorldClimateEngine climateEngine;
            try {
                climateEngine = ClimateEngine.getInstance().getClimateEngine(gPlayer.getPlayer().getWorld().getName());
            } catch (NullPointerException e) {
                gPlayer.sendMsg(Lang.ADMIN_RELOAD_INVALID_WORLD);
                return;
            }

            if (args.length == 0) {
                for (Model model : climateEngine.getModels().values()) {
                    model.loadModel();
                }
                gPlayer.sendMsg(Lang.ADMIN_RELOAD_SUCCESS, "all models");
            } else {
                String name = args[0];

                for (Model model : climateEngine.getModels().values()) {
                    if (model.getModelName().replace(".json", "").equalsIgnoreCase(name)) {
                        model.loadModel();
                        gPlayer.sendMsg(Lang.ADMIN_RELOAD_SUCCESS, name);
                        return;
                    }
                }
                gPlayer.sendMsg(Lang.ADMIN_RELOAD_INVALID_MODEL);
            }
        }

        @Default
        @Subcommand("all")
        @Description("Reload all configs and models")
        public void reloadAll(GPlayer gPlayer) {
            // Models
            try {
                WorldClimateEngine climateEngine = ClimateEngine.getInstance().getClimateEngine(gPlayer.getPlayer().getWorld().getName());
                for (Model model : climateEngine.getModels().values()) {
                    model.loadModel();
                }
            } catch (NullPointerException e) {
                gPlayer.sendMsg(Lang.ADMIN_RELOAD_INVALID_WORLD);
            }

            // Config
            Lang.init();
            GlobalWarming.getInstance().getConf().load();

            gPlayer.sendMsg(Lang.ADMIN_RELOAD_SUCCESS, "all");
        }

    }

    @Subcommand("debug")
    @CommandPermission("globalwarming.admin.debug")
    public class DebugCommands extends BaseCommand {

        @Subcommand("database|db")
        @Description("Toggles database console logging")
        public void onDatabaseDebug(GPlayer gPlayer, String[] args) {
            boolean value = AsyncDBQueue.getInstance().isDebug();
            AsyncDBQueue.getInstance().setDebug(!value);

            if (!value) {
                gPlayer.sendMsg(ChatColor.GREEN + "Database console logging = " +  ChatColor.YELLOW + "true.");
            } else {
                gPlayer.sendMsg(ChatColor.GREEN + "Database console logging = " + ChatColor.GRAY + "false.");
            }
        }

        @Subcommand("effect")
        @CommandPermission("globalwarming.admin.debug.effect")
        public class EffectCommands extends BaseCommand {

            @Subcommand("sealevel")
            @Syntax("[level]")
            @Description("Force execute Sea Level effect")
            public void onSeaLevel(GPlayer gPlayer, String[] args) {
                if (args.length < 2) {
                    int seaLevel = 62;

                    if (args.length == 1) {
                        try {
                            seaLevel = Integer.parseInt(args[0]);
                        } catch (NumberFormatException e) {
                            gPlayer.sendMsg(ChatColor.RED + "Invalid SeaLevel");
                            return;
                        }
                    }

                    Player player = gPlayer.getPlayer();
                    Supplier<HashSet<BlockChange>> changes = EffectEngine.getInstance().getEffect(SeaLevelRise.class, ClimateEffectType.SEA_LEVEL_RISE).execute(player.getLocation().getChunk().getChunkSnapshot(), seaLevel);
                    new SyncChunkUpdateTask(player.getLocation().getChunk(), CompletableFuture.supplyAsync(changes)).runTaskLater(GlobalWarming.getInstance(), 40L);
                    gPlayer.sendMsg(ChatColor.GREEN + String.format("Applying sea level rise from y:%d to chunk", seaLevel));
                } else {
                    gPlayer.sendMsg(ChatColor.RED + "Invalid Args");
                }
            }
        }
    }
}
