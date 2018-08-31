package net.porillo.engine.models;

import net.porillo.engine.api.Model;
import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;

import java.util.ArrayList;
import java.util.List;

public class ScoreTempModel extends Model {

	private double[] scores;
	private double[] temps;

	private PolynomialSplineFunction splineFunction;

	public ScoreTempModel() {
		super("scoreTempModel.txt");
		this.loadModel();
	}

	@Override
	public void loadModel() {
		List<Integer> scores = new ArrayList<>();
		List<Double> temps = new ArrayList<>();

		for (String line : super.getLines()) {
			String[] args = line.split(":");
			scores.add(Integer.parseInt(args[0]));
			temps.add(Double.parseDouble(args[1]));
		}

		this.scores = new double[scores.size()];
		this.temps = new double[temps.size()];

		for (int i = 0; i < scores.size(); i++) {
			this.scores[i] = (double) scores.get(i);
			this.temps[i] = temps.get(i);
		}

		this.splineFunction = new LinearInterpolator().interpolate(this.scores, this.temps);
	}

	public double getTemperature(int score) {
		return splineFunction.value((double) score);
	}
}
