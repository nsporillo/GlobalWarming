package net.porillo.engine.api;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Distribution {

    public double[] temp;
    public double[] fitness;
    @Getter private transient List<Double> x, y;

    public Distribution(List<Double> x, List<Double> y) {
        this.x = x;
        this.y = y;
    }

    public Distribution(double[] temp, double[] fitness) {
        this.x = new ArrayList<>();
        this.y = new ArrayList<>();
        Arrays.stream(temp).forEach(d -> this.x.add(d));
        Arrays.stream(fitness).forEach(d -> this.y.add(d));
    }

    public double getValue(double input) {
        // Compatibility
        if (x == null || y == null) {
            this.x = new ArrayList<>();
            this.y = new ArrayList<>();
            Arrays.stream(temp).forEach(d -> this.x.add(d));
            Arrays.stream(fitness).forEach(d -> this.y.add(d));
        }

        if (input <= x.get(0)) {
            return y.get(0);
        } else if (input >= x.get(x.size() - 1)) {
            return y.get(y.size() - 1);
        }

        for (int i = 0; i < x.size(); i++) {
            if (input < x.get(i)) {
                return y.get(i - 1) + (input - x.get(i - 1)) / (x.get(i) - x.get(i - 1)) * (y.get(i) - y.get(i - 1));
            }
        }
        return -1;
    }
}
