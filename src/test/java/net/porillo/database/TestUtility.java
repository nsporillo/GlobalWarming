package net.porillo.database;

import lombok.Getter;
import net.porillo.objects.Furnace;
import net.porillo.objects.GPlayer;
import net.porillo.objects.GWorld;

import java.util.Random;
import java.util.UUID;

public class TestUtility {

    private static TestUtility instance;

    @Getter private final ConnectionManager connectionManager;
    @Getter private final Random random = new Random();

    public TestUtility() {
        this.connectionManager = new ConnectionManager("localhost", 3306, "GlobalWarming", "jenkins", "tests");
    }

    public GWorld nextRandomWorld() {
        GWorld gWorld = new GWorld();
        gWorld.setUniqueID(random.nextInt(Integer.MAX_VALUE));
        gWorld.setWorldId(UUID.randomUUID());
        gWorld.setTemperature(14.0 + random.nextInt(2));
        gWorld.setFirstSeen(random.nextLong());
        gWorld.setCarbonValue(random.nextInt(16777215));
        gWorld.setSeaLevel(random.nextInt(255));
        gWorld.setSize(random.nextInt(65535));
        return gWorld;
    }

    public GPlayer nextRandomPlayer() {
        GPlayer gPlayer = new GPlayer();
        gPlayer.setUniqueId(random.nextInt(Integer.MAX_VALUE));
        gPlayer.setUuid(UUID.randomUUID());
        gPlayer.setCarbonScore(random.nextInt(16777215));
        gPlayer.setFirstSeen(0);
        gPlayer.setWorldId(UUID.randomUUID());
        return gPlayer;
    }

    public Furnace nextRandomFurnace() {
        Furnace furnace = new Furnace();
        furnace.setUniqueId(random.nextInt(Integer.MAX_VALUE));
        furnace.setOwnerId(random.nextInt(Integer.MAX_VALUE));
        //TODO: Update furnace class to use worldId,x,y,z for location
        return furnace;
    }

    public static TestUtility getInstance() {
        if (instance == null) {
            instance = new TestUtility();
        }
        return instance;
    }
}
