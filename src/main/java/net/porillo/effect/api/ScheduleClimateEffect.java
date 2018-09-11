package net.porillo.effect.api;

import lombok.Getter;
import lombok.Setter;

public abstract class ScheduleClimateEffect extends ClimateEffect implements Runnable {

    @Getter @Setter private int period;
    @Getter @Setter private int taskId;

}
