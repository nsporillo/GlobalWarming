package net.porillo.effect.api;

import net.porillo.GlobalWarming;
import org.bukkit.event.Listener;

public abstract class ListenerClimateEffect extends ClimateEffect implements Listener {

    @Override
    public void onPluginEnable() {
        GlobalWarming.getInstance().getLogger().info("Loading Climate Effect " + super.getName());
    }

    @Override
    public void onPluginDisable() {
        GlobalWarming.getInstance().getLogger().info("Unloading Climate Effect " + super.getName());
    }
}
