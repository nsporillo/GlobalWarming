package net.porillo.engine.api;

public class FireDistribution extends Distribution {

    public FireDistribution(double[] temp, double[] fitness, double[] blocks) {
        super(temp, blocks);
    }

    public double getBlocks(double input) {
        return super.getValue(input);
    }
}
