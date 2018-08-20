package net.porillo.database.tables;

import lombok.Getter;
import net.porillo.objects.Furnace;
import net.porillo.objects.GLocation;
import net.porillo.objects.GPlayer;
import net.porillo.objects.Tree;
import org.bukkit.World;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class TreeTable extends Table {

	@Getter
	private Map<GLocation, Tree> locationMap = new HashMap<>();
	@Getter
	private Map<GPlayer, HashSet<Tree>> playerMap = new HashMap<>();

	public TreeTable() {
		super("trees");
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
