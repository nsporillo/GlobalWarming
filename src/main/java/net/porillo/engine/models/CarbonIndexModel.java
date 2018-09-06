package net.porillo.engine.models;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.Getter;
import net.porillo.engine.api.Model;
import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

public class CarbonIndexModel extends Model {

	@Getter private Map<Integer, Double> indexMap;

	private double[] scores;
	private double[] indices;
	private PolynomialSplineFunction splineFunction;

	private Gson gson;

	public CarbonIndexModel() {
		super("carbonindexmodel.json");
		this.gson = new GsonBuilder().setPrettyPrinting().create();
		this.loadModel();
	}

	@Override
	public void loadModel() {
		this.indexMap = new TreeMap<>(Comparator.naturalOrder());
		this.indexMap.putAll(this.gson.fromJson(super.getContents(), new TypeToken<Map<Integer, Double>>() {}.getType()));

		this.scores = new double[indexMap.size()];
		this.indices = new double[indexMap.size()];

		int i = 0;
		for (Map.Entry<Integer, Double> entry : indexMap.entrySet()) {
			scores[i] = entry.getKey();
			indices[i++] = entry.getValue();
		}

		this.splineFunction = new LinearInterpolator().interpolate(this.scores, this.indices);
	}

	public double getCarbonIndex(int score) {
		return splineFunction.value(score);
	}
}
