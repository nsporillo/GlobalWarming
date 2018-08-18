package net.porillo.database.tables;

import lombok.Getter;
import net.porillo.objects.World;

import java.util.ArrayList;
import java.util.List;

public class WorldTable extends Table {

	@Getter  private List<World> worlds = new ArrayList<>();

	public WorldTable() {
		super("worlds");
	}

	@Override
	public void createIfNotExists() {

	}

	public void addWorld(org.bukkit.World world) {

	}

	@Override
	public List<World> loadTable() {
		return null;
	}
}
