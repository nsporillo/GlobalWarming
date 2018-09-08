package net.porillo;

import net.porillo.engine.models.CarbonIndexModel;
import net.porillo.engine.models.ContributionModel;
import net.porillo.engine.models.EntityFitnessModel;
import net.porillo.engine.models.ScoreTempModel;
import org.testng.annotations.Test;

@Test
public class ModelTest {

	@Test
	public void testContributionModel() {
		ContributionModel model = new ContributionModel();
		model.loadModel();
	}

	@Test
	public void testEntityFitnessModel() {
		EntityFitnessModel model = new EntityFitnessModel();
		model.loadModel();
	}

	@Test
	public void testScoreTempModel() {
		ScoreTempModel model = new ScoreTempModel();
		model.loadModel();

		// Test the interpolator
		// If any of the points aren't monotonically increasing, an exception is thrown
		for (int i = 1; i < 1000000; i+=1000) {
			model.getTemperature(i);
		}
	}

	public void testScoreIndexModel() {
		CarbonIndexModel model = new CarbonIndexModel();
		model.loadModel();

		// Test the interpolator
		// If any of the points aren't monotonically increasing, an exception is thrown
		for (int i = -1000000; i < 1000000; i+=1000) {
			//System.out.println(String.format("%d - %4.3f", i, model.getCarbonIndex(i)));
			model.getCarbonIndex(i);
		}
	}
}
