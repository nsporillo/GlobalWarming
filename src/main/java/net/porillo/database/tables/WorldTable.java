package net.porillo.database.tables;

import lombok.Getter;
import net.porillo.objects.World;

import java.util.ArrayList;
import java.util.List;

public class WorldTable extends Table {

	@Getter private List<World> worlds = new ArrayList<>();

	public WorldTable() {
		super("worlds");
	}

	@Override
	public void createIfNotExists() {

	}
	
	public World getWorld(String name) {
		for (World world : worlds) {
			if (world.getWorldName().equals(name)) {
				return world;
			}
		}

		return null;
	}
	
	public void addWorld(World world) {
		this.worlds.add(world);
	}

	public List<World> loadTable() {
		return null;
	}
}
