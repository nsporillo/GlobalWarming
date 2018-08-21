package net.porillo.database.tables;

import lombok.Getter;
import net.porillo.objects.Furnace;

import net.porillo.objects.GPlayer;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class FurnaceTable extends Table {

	@Getter private Map<Location, Furnace> locationMap = new HashMap<>();
	@Getter private Map<GPlayer, HashSet<Furnace>> playerMap = new HashMap<>();

	public FurnaceTable() {
		super("furnaces");
	}

	@Override
	public void createIfNotExists() {

	}

    public void addWorld(World world) {

	}

	public List<Furnace> loadTable() {
		return null;
	}
}
