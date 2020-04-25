package net.porillo.engine.models;

import com.google.gson.reflect.TypeToken;
import lombok.Getter;
import net.porillo.GlobalWarming;
import net.porillo.engine.ClimateEngine;
import net.porillo.engine.api.Distribution;
import net.porillo.engine.api.Model;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

public class CarbonIndexModel extends Model {

    @Getter private Map<Integer, Double> indexMap;
    private Distribution distribution;

    public CarbonIndexModel(String worldName) {
        super(worldName, "carbonIndexModel.json");
        this.loadModel();
    }

    @Override
    public void loadModel() {
        this.indexMap = new TreeMap<>(Comparator.naturalOrder());
        this.indexMap.putAll(GlobalWarming.getInstance().getGson()
                .fromJson(super.getContents(), new TypeToken<Map<Integer, Double>>() {
                }.getType()));

        double[] scores = new double[indexMap.size()];
        double[] indices = new double[indexMap.size()];

        int i = 0;
        for (Map.Entry<Integer, Double> entry : indexMap.entrySet()) {
            scores[i] = (double) entry.getKey();
            indices[i++] = entry.getValue();
        }

        this.distribution = new Distribution(scores, indices);
    }

    public double getCarbonIndex(int score) {
        return distribution.getValue((double) score);
    }
}
