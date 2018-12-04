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
}
