package net.porillo.engine.api;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

@Test
public class DistributionTest {

    private final static List<Double> x = Arrays.asList(0D, 1D, 2D, 3D, 4D);
    private final static List<Double> y = Arrays.asList(0D, 10D, 25D, 50D, 100D);
    @Test
    public void testDistributionMin() {
        Distribution distribution = new Distribution(x,y);
        Assert.assertEquals(distribution.getValue(0), 0D);
        Assert.assertEquals(distribution.getValue(1), 10D);
        Assert.assertEquals(distribution.getValue(2), 25D);
        Assert.assertEquals(distribution.getValue(3), 50D);
        System.out.println(distribution.getValue(3.95));
        Assert.assertEquals(distribution.getValue(4), 100D);
    }
}
