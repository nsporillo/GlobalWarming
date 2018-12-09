package net.porillo.engine.api;

import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;

public class FireDistribution extends Distribution {

    private double[] blocks;
    private transient PolynomialSplineFunction splineFunction;

    public FireDistribution(double[] temp, double[] fitness, double[] blocks) {
        super(temp, fitness);
        this.blocks = blocks;
        this.splineFunction = new SplineInterpolator().interpolate(getTemp(), blocks);
    }

    public double getBlocks(double input) {
        if (this.splineFunction == null) {
            this.splineFunction = new SplineInterpolator().interpolate(getTemp(), blocks);
        }

        return this.splineFunction.value(input);
    }
}
