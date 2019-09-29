package net.porillo.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.porillo.GlobalWarming;
import net.porillo.database.tables.PlayerTable;
import net.porillo.database.tables.WorldTable;
import net.porillo.engine.ClimateEngine;
import net.porillo.engine.api.WorldClimateEngine;
import net.porillo.objects.GPlayer;
import net.porillo.objects.GWorld;
import net.porillo.util.Colorizer;
import org.bukkit.entity.Player;

public class TemperatureExpansion extends PlaceholderExpansion {

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public boolean register() {
        GlobalWarming.getInstance().getLogger().info("Temperature Expansion Placeholder Expansion loaded");
        return super.register();
    }

    @Override
    public String getIdentifier() {
        return "GlobalWarming";
    }

    @Override
    public String getAuthor() {
        return "milkywayz";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public String onPlaceholderRequest(Player p, String identifier) {
        if (p != null && p.isOnline()) {
            if (identifier.equalsIgnoreCase("world_temp")) {
                WorldClimateEngine climateEngine = ClimateEngine.getInstance().getClimateEngine(p.getWorld().getUID());
                return Colorizer.formatTemp(climateEngine.getTemperature());
            } else if (identifier.equalsIgnoreCase("world_score")) {
                WorldTable worldTable = GlobalWarming.getInstance().getTableManager().getWorldTable();
                GWorld gWorld = worldTable.getWorld(p.getWorld().getUID());
                return String.valueOf(gWorld.getCarbonValue());
            } else if (identifier.equalsIgnoreCase("player_score")) {
                PlayerTable playerTable = GlobalWarming.getInstance().getTableManager().getPlayerTable();
                GPlayer gPlayer = playerTable.getPlayers().get(p.getUniqueId());
                if (gPlayer != null) {
                    return Colorizer.formatScore(gPlayer.getCarbonScore());
                }
            }
        }
        return null;
    }
}
