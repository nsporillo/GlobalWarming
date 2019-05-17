package net.porillo;

import net.porillo.engine.models.*;
import org.testng.annotations.Test;

import static net.porillo.engine.models.ScoreTempModel.CarbonSensitivity;

@Test
public class ModelTest {

    private static final String world = "world";

    @Test
    public void testFuelModel() {
        FuelModel model = new FuelModel(world);
    }

    @Test
    public void testMethaneModel() {
        EntityMethaneModel methaneModel = new EntityMethaneModel(world);
    }

    @Test
    public void testReductionModel() {
        ReductionModel model = new ReductionModel(world);
    }

    @Test
    public void testEntityFitnessModel() {
        EntityFitnessModel model = new EntityFitnessModel(world);
    }

    @Test
    public void testScoreTempModel() {
        ScoreTempModel model = new ScoreTempModel(world, CarbonSensitivity.LOW);

        // Test the interpolator
        // If any of the points aren't monotonically increasing, an exception is thrown
        for (int i = 1; i < 1000000; i += 1000) {
            model.getTemperature(i);
        }
    }

    public void testScoreIndexModel() {
        CarbonIndexModel model = new CarbonIndexModel(world);
        model.loadModel();

        // Test the interpolator
        // If any of the points aren't monotonically increasing, an exception is thrown
        for (int i = -1000000; i < 1000000; i += 1000) {
            //System.out.println(String.format("%d - %4.3f", i, model.getCarbonIndex(i)));
            model.getCarbonIndex(i);
        }
    }
}
