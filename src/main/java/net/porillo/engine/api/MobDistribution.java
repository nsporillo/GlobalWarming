package net.porillo.engine.api;

import lombok.Getter;

public class MobDistribution extends Distribution {
    @Getter private String alternate;

    public MobDistribution(double[] temp, double[] fitness, String alternate) {
        super(temp, fitness);
        this.alternate = alternate;
    }
}
