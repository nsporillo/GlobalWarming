package net.porillo.engine.api;

import lombok.Getter;

public class AlternateDistribution extends Distribution {
    @Getter
    private String alternate;

    public AlternateDistribution(double[] temp, double[] fitness, String alternate) {
        super(temp, fitness);
        this.alternate = alternate;
    }
}
