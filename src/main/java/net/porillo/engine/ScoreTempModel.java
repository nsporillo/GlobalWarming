package net.porillo.engine;

import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ScoreTempModel extends Model {

	private double[] scores;
	private double[] temps;

	private PolynomialSplineFunction splineFunction;

	public ScoreTempModel() {
		super("scoreTempModel");
		this.loadModel();
	}

	@Override
	public void loadModel() {
		try {
			BufferedReader reader = super.getReader();

			String line;
			List<Integer> scores = new ArrayList<>();
			List<Double> temps = new ArrayList<>();
			while ((line = reader.readLine()) != null) {
				String[] args = line.split(":");
				scores.add(Integer.parseInt(args[0]));
				temps.add(Double.parseDouble(args[1]));
			}

			this.scores = new double[scores.size() + 1];
			this.temps = new double[temps.size() + 1];

			for(int i = 0; i < scores.size(); i++) {
				this.scores[i] = (double) scores.get(i);
				this.temps[i] = temps.get(i);
			}

			this.splineFunction = new LinearInterpolator().interpolate(this.scores, this.temps);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public double getTemperature(int score) {
		return splineFunction.value((double)score);
	}
}
