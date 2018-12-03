package net.porillo;

import net.porillo.engine.models.*;
import static net.porillo.engine.models.ScoreTempModel.CarbonSensitivity;

import org.testng.annotations.Test;

import java.util.UUID;

@Test
public class ModelTest {

    @Test
    public void testContributionModel() {
        UUID worldId = UUID.randomUUID();
        ContributionModel model = new ContributionModel(worldId);
    }

    @Test
    public void testReductionModel() {
        UUID worldId = UUID.randomUUID();
        ReductionModel model = new ReductionModel(worldId);
    }

    @Test
    public void testEntityFitnessModel() {
        UUID worldId = UUID.randomUUID();
        EntityFitnessModel model = new EntityFitnessModel(worldId);
    }

    @Test
    public void testScoreTempModel() {
        UUID worldId = UUID.randomUUID();
        ScoreTempModel model = new ScoreTempModel(worldId, CarbonSensitivity.LOW);

        // Test the interpolator
        // If any of the points aren't monotonically increasing, an exception is thrown
        for (int i = 1; i < 1000000; i += 1000) {
            model.getTemperature(i);
        }
    }

    public void testScoreIndexModel() {
        UUID worldId = UUID.randomUUID();
        CarbonIndexModel model = new CarbonIndexModel(worldId);
        model.loadModel();

        // Test the interpolator
        // If any of the points aren't monotonically increasing, an exception is thrown
        for (int i = -1000000; i < 1000000; i += 1000) {
            //System.out.println(String.format("%d - %4.3f", i, model.getCarbonIndex(i)));
            model.getCarbonIndex(i);
        }
    }
}
