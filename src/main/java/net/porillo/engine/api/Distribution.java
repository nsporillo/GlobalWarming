package net.porillo.engine.api;

import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;

public class Distribution {

	private double[] temp;
	private double[] fitness;
	private transient PolynomialSplineFunction splineFunction;

	public Distribution(double[] temp, double[] fitness) {
		this.temp = temp;
		this.fitness = fitness;
		this.splineFunction = new SplineInterpolator().interpolate(temp, fitness);
	}

	public double getValue(double input) {
		if (splineFunction == null) {
			splineFunction = new SplineInterpolator().interpolate(temp, fitness);
		}
		return splineFunction.value(input);
	}
	
	public static Distribution sampleDistribution() {
		final double[] temp = {10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20};
		final double[] fitness = {20, 40, 60, 80, 100, 95, 90, 75, 50, 30, 10};
		return new Distribution(temp, fitness);
	}
}
