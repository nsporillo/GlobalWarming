package net.porillo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.porillo.engine.models.FuelModel;
import org.bukkit.Material;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

public class FuelModelTest {

    @Test
    public void fuelModelContainsAllMaterials() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        FuelModel model = new FuelModel(gson, "world");
        Map<Material, Double> existingFuelMap = model.getFuelMap();
        for (Material material : Material.values()) {
            if (material.isFuel()) {
                Assert.assertTrue(existingFuelMap.containsKey(material), material.name());
            }
        }
    }
}
