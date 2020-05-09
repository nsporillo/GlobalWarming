package net.porillo.effect.api;

import lombok.Getter;
import lombok.Setter;
import net.porillo.GlobalWarming;

public abstract class ScheduleClimateEffect extends ClimateEffect implements Runnable {

    @Getter @Setter private int period;
    @Getter @Setter private int taskId;

    @Override
    public void onPluginEnable() {
        GlobalWarming.getInstance().getLogger().info("Loading Climate Effect " + super.getName());
    }

    @Override
    public void onPluginDisable() {
        GlobalWarming.getInstance().getLogger().info("Unloading Climate Effect " + super.getName());
    }
}
