package net.porillo.engine.models;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.Getter;
import net.porillo.engine.api.Model;
import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;

import java.lang.reflect.Type;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

public class ScoreTempModel extends Model {

    /**
     * Carbon sensitivity
     * - LOW: [-1M, 1M] (e.g., many players)
     * - HIGH: [-50K, 50K] (e.g., fewer players)
     */
    public enum CarbonSensitivity {
        LOW,
        HIGH
    }

    @Getter private Map<Integer, Double> indexMap;
    private PolynomialSplineFunction splineFunction;
    private CarbonSensitivity sensitivity;
    Map<CarbonSensitivity, Map<Integer, Double>> temperatureMap;

    public ScoreTempModel(UUID worldId, CarbonSensitivity sensitivity) {
        super(worldId, "scoreTempModel.json");
        this.sensitivity = sensitivity;
        this.loadModel();
    }

    public Map<Integer, Double> getTemperatureMap() {
        return temperatureMap.get(sensitivity);
    }

    /**
     * Load the score / temperature model
     * - CarbonSensitivity specifies the range of scores being mapped onto [10C, 20C]
     */
    @Override
    public void loadModel() {
        //Deserialize the model from JSON:
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.enableComplexMapKeySerialization().create();
        Type listType = new TypeToken<Map<CarbonSensitivity, Map<Integer, Double>>>() {}.getType();
        temperatureMap = gson.fromJson(super.getContents(), listType);

        //Validate the result:
        this.indexMap = new TreeMap<>(Comparator.naturalOrder());
        if (temperatureMap == null || !temperatureMap.containsKey(sensitivity)) {
            throw new RuntimeException(String.format("Invalid score-temperature model (%s): [%s]", sensitivity, super.getPath()));
        }

        if (temperatureMap.get(sensitivity).isEmpty()) {
            throw new RuntimeException(String.format("No values found in (%s): [%s]", sensitivity, super.getPath()));
        }

        //Copy the result:
        this.indexMap.putAll(temperatureMap.get(sensitivity));

        //Create a lookup function:
        int i = 0;
        double[] scores = new double[indexMap.size()];
        double[] temps = new double[indexMap.size()];
        for (Map.Entry<Integer, Double> entry : indexMap.entrySet()) {
            scores[i] = entry.getKey();
            temps[i++] = entry.getValue();
        }

        this.splineFunction = new LinearInterpolator().interpolate(scores, temps);
    }

    /**
     * Get the temperature using linear interpolation
     * - Limits score to the domain to prevent exceptions
     * - This value is used by most models
     */
    public double getTemperature(int score) {
        double boundedScore =
              Math.max(splineFunction.getKnots()[0],
              Math.min((double)score, splineFunction.getKnots() [splineFunction.getN()]));
        return splineFunction.value(boundedScore);
    }
}
