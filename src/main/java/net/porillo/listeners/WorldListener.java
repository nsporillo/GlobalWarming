package net.porillo.listeners;

import lombok.RequiredArgsConstructor;
import net.porillo.GlobalWarming;
import net.porillo.engine.ClimateEngine;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;

@RequiredArgsConstructor
public class WorldListener implements Listener {

    private final GlobalWarming gw;

    @EventHandler(ignoreCancelled = true)
    public void onWorldLoad(WorldLoadEvent loadEvent) {
        gw.getLogger().info("Detected world load after GW enabled, triggering automatic climate engine load.");
        ClimateEngine.getInstance().loadWorldClimateEngine(loadEvent.getWorld());
    }
}
