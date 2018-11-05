package net.porillo.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import net.porillo.GlobalWarming;

import net.porillo.config.Lang;
import net.porillo.database.api.SelectCallback;
import net.porillo.database.queries.select.TopPlayersQuery;
import net.porillo.database.queue.AsyncDBQueue;
import net.porillo.database.tables.OffsetTable;
import net.porillo.engine.ClimateEngine;
import net.porillo.engine.api.WorldClimateEngine;
import net.porillo.engine.models.CarbonIndexModel;
import net.porillo.objects.GPlayer;
import net.porillo.objects.OffsetBounty;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.bukkit.ChatColor.*;

@CommandAlias("globalwarming|gw")
public class GeneralCommands extends BaseCommand {
	private Map<UUID, Long> lastTopped = new HashMap<>();
	private static final UUID untrackedUUID = UUID.fromString("1-1-1-1-1");
	private static final ChatColor[] topHeader = {GOLD, AQUA, LIGHT_PURPLE, AQUA, GOLD, AQUA, LIGHT_PURPLE, AQUA, GOLD,
			AQUA, LIGHT_PURPLE, AQUA, GOLD};

    @Subcommand("score")
    @Description("Get your carbon score")
    @CommandPermission("globalwarming.score")
    public void onScore(GPlayer gPlayer) {
        Player player = gPlayer.getPlayer();
        if (player != null) {
            //Do not show scored for worlds with disabled climate-engines:
            // - Note: temperature is based on the player's associated-world (not the current world)
            WorldClimateEngine associatedClimateEngine =
                  ClimateEngine.getInstance().getAssociatedClimateEngine(player);

            if (associatedClimateEngine != null && associatedClimateEngine.isEnabled()) {
                int score = gPlayer.getCarbonScore();
                double temperature = associatedClimateEngine.getTemperature();
                gPlayer.sendMsg(
                      Lang.SCORE_CHAT,
                      formatScore(score),
                      formatTemperature(temperature));

                //Guidance based on the global temperature:
                if (temperature < 13.75) {
                    gPlayer.sendMsg(Lang.TEMPERATURE_LOW);
                } else if (temperature < 14.25) {
                    gPlayer.sendMsg(Lang.TEMPERATURE_BALANCED);
                } else {
                    gPlayer.sendMsg(Lang.TEMPERATURE_HIGH);
                }
            } else {
                gPlayer.sendMsg(Lang.ENGINE_DISABLED);
            }
        }
    }

	@Subcommand("top")
	@Description("Display the top ten players")
	@CommandPermission("globalwarming.top")
	public void onTop(GPlayer gPlayer) {
		// Prevent players from spamming /gw top (which syncs the database)
		if (lastTopped.containsKey(gPlayer.getUuid())) {
			Long last = lastTopped.get(gPlayer.getUuid());
			long diff = System.currentTimeMillis() - last;

			if (diff < 3000) {
				gPlayer.sendMsg(RED + "Please wait " + YELLOW + (3000 - diff) / 1000 + RED + " seconds to view top again.");
			} else {
				lastTopped.remove(gPlayer.getUuid());
				onTop(gPlayer);
			}
		} else {
			lastTopped.put(gPlayer.getUuid(), System.currentTimeMillis());

			Player player = Bukkit.getPlayer(gPlayer.getUuid());
			String worldName = player.getWorld().getName();

			if (ClimateEngine.getInstance().getClimateEngine(worldName).isEnabled()) {
				CarbonIndexModel indexModel = ClimateEngine.getInstance().getClimateEngine(worldName).getCarbonIndexModel();

				AsyncDBQueue.getInstance().queueSelectQuery(new TopPlayersQuery("players", (SelectCallback<Object[]>) returnList -> {
					String header = "%s+%s------ %splayer%s ------%s+%s-- %sindex%s --%s+%s-- %sscore%s --%s+";
					String footer = "%s+%s------------------%s+%s-----------%s+%s-----------%s+";
					gPlayer.sendMsg(String.format(header, topHeader));

					String row = DARK_PURPLE + "%d " + WHITE + "%s " + GOLD + "+ %s " + GOLD + "+ %s " + GOLD + "+";
					int i = 1;
					for (Object[] result : returnList) {
						UUID uuid = UUID.fromString((String) result[0]);
						int score = (int) result[1];
						double index = indexModel.getCarbonIndex(score);
						String playerName;

						if (uuid.equals(untrackedUUID)) {
							playerName = "Untracked";
						} else {
							playerName = Bukkit.getOfflinePlayer(uuid).getName();
						}

						int pad = i == 10 ? 22 : 23;
						gPlayer.sendMsg(String.format(row, i++, fixed(playerName, pad),
								fixed(formatIndex(index, score), 13),
								fixed(formatScore(score), 12)));
					}

					gPlayer.sendMsg(String.format(footer, GOLD, AQUA, GOLD, AQUA, GOLD, AQUA, GOLD));
				}));
			} else {
                gPlayer.sendMsg(Lang.ENGINE_DISABLED);
			}
		}
	}

    @Subcommand("bounty")
    @CommandPermission("globalwarming.bounty")
    public class BountyCommand extends BaseCommand {

        @Subcommand("offset")
        @Description("Set tree-planting bounties to reduce carbon footprint")
        @Syntax("[log] [reward]")
        @CommandPermission("globalwarming.bounty.offset")
        public void onBountyOffset(GPlayer gPlayer, String[] args) {
            // Validate input
            Integer logTarget;
            Integer reward;

            if (args.length != 2) {
                gPlayer.sendMsg(RED + "Must specify 2 args");
            }
            try {
                logTarget = Integer.parseInt(args[0]);
                reward = Integer.parseInt(args[1]);

                if (logTarget <= 0 || reward <= 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException nfe) {
                gPlayer.sendMsg(RED + "Error: <trees> and <reward> must be positive integers");
                return;
            }

            //TODO: Add economy integration
            OffsetBounty bounty = new OffsetBounty();
            bounty.setCreator(gPlayer);
            bounty.setLogBlocksTarget(logTarget);
            bounty.setReward(reward);
        }

        // TODO: When listing bounties, add a clickable chat link to easily start job
        // TODO: Add configurable player max concurrent bounties to prevent bounty hoarding
        @Subcommand("list")
        @Description("Show all current bounties")
        @Syntax("")
        @CommandPermission("globalwarming.bounty.list")
        public void onBounty(GPlayer gPlayer) {
            OffsetTable offsetTable = GlobalWarming.getInstance().getTableManager().getOffsetTable();
            Player player = gPlayer.getPlayer();

            int numBounties = offsetTable.getOffsetList().size();
            gPlayer.sendMsg(GREEN + "Showing " + numBounties + " Tree Planting Bounties");

            // TODO: Paginate if necessary
            for (OffsetBounty bounty : offsetTable.getOffsetList()) {
                if (bounty.isAvailable()) {
                    //bounty.showPlayerDetails(player);
                }
            }
        }
    }

    @Subcommand("scoreboard")
    @CommandPermission("globalwarming.scoreboard")
    public class ScoreboardCommand extends BaseCommand {

        @Subcommand("show")
        @Description("Show the scoreboard")
        @Syntax("")
        @CommandPermission("globalwarming.scoreboard.show")
        public void onShow(GPlayer gPlayer) {
            Player player = gPlayer.getPlayer();
            GlobalWarming.getInstance().getScoreboard().show(player, true);
        }

        @Subcommand("hide")
        @Description("Show the scoreboard")
        @Syntax("")
        @CommandPermission("globalwarming.scoreboard.hide")
        public void onHide(GPlayer gPlayer) {
            Player player = gPlayer.getPlayer();
            GlobalWarming.getInstance().getScoreboard().show(player, false);
        }
    }

    @HelpCommand
    public void onHelp(GPlayer gPlayer, CommandHelp help) {
        help.showHelp();
    }


    private static String fixed(String text, int length) {
        return String.format("%-" + length + "." + length + "s", text);
    }

    private String formatIndex(double index, int score) {
        return String.format("%s%1.4f",
              getScoreColor(score),
              index);
    }

    private String formatScore(int score) {
        return String.format("%s%d",
              getScoreColor(score),
              score);
    }

    /**
     * Using color-heat to map LOW CO2 (cold) to HIGH CO2 (hot) values
     *  - Currently, these values are arbitrary
     */
    public static ChatColor getScoreColor(int score) {
        ChatColor color;
        if (score <= -3500) {
            color = DARK_BLUE;
        } else if (score <= -2500) {
            color = BLUE;
        } else if (score <= -1500) {
            color = DARK_AQUA;
        } else if (score <= -500) {
            color = AQUA;
        } else if (score <= 500) {
            color = GREEN; // (-500, 500]
        } else if (score <= 1500) {
            color = YELLOW;
        } else if (score <= 2500) {
            color = GOLD;
        } else if (score <= 3500) {
            color = RED;
        } else {
            color = DARK_RED;
        }

        return color;
    }

    public static ChatColor getTemperatureColor(double temperature) {
        ChatColor color;
        if (temperature <= 10.5) {
            color = DARK_BLUE;
        } else if (temperature <= 11.5) {
            color = BLUE;
        } else if (temperature <= 12.5) {
            color = DARK_AQUA;
        } else if (temperature <= 13.5) {
            color = AQUA;
        } else if (temperature <= 14.5) {
            color = GREEN; // (13.5, 14.5]
        } else if (temperature <= 15.5) {
            color = YELLOW;
        } else if (temperature <= 16.5) {
            color = GOLD;
        } else if (temperature <= 17.5) {
            color = LIGHT_PURPLE;
        } else if (temperature <= 18.5) {
            color = RED;
        } else {
            color = DARK_RED;
        }

        return color;
    }

    private String formatTemperature(double temperature) {
        ChatColor color = getTemperatureColor(temperature);
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        return String.format("%s%s",
              color,
              decimalFormat.format(temperature));
    }
}
