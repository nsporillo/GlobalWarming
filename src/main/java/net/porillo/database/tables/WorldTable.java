package net.porillo.database.tables;

import lombok.Getter;
import net.porillo.objects.GWorld;

import java.util.ArrayList;
import java.util.List;

public class WorldTable extends Table {

    @Getter
    private List<GWorld> worlds = new ArrayList<>();

	public WorldTable() {
        super("worlds");
	}

	@Override
	public void createIfNotExists() {

	}

    public GWorld getWorld(String name) {
        for (GWorld world : worlds) {
            if (world.getWorldName().equals(name)) {
                return world;
			}
		}

		return null;
	}

    public void addWorld(GWorld world) {
        this.worlds.add(world);
    }

    public List<GWorld> loadTable() {
		return null;
	}
}
