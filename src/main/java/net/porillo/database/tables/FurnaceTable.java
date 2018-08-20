package net.porillo.database.tables;

import lombok.Getter;
import net.porillo.objects.Furnace;
import net.porillo.objects.World;

import java.util.ArrayList;
import java.util.List;

public class FurnaceTable extends Table {

	@Getter  private List<Furnace> furnaces = new ArrayList<>();

	public FurnaceTable() {
		super("furnaces");
	}

	@Override
	public void createIfNotExists() {

	}

	public void addWorld(org.bukkit.World world) {

	}

	@Override
	public List<Furnace> loadTable() {
		return null;
	}
}
