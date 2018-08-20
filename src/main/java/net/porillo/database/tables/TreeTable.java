package net.porillo.database.tables;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import net.porillo.objects.Furnace;
import net.porillo.objects.Location;
import net.porillo.objects.Player;
import net.porillo.objects.Tree;

public class TreeTable extends Table {

	@Getter private Map<Location, Tree> locationMap = new HashMap<>();
	@Getter private Map<Player, HashSet<Tree>> playerMap = new HashMap<>();

	public TreeTable() {
		super("trees");
	}

	@Override
	public void createIfNotExists() {

	}

	public void addWorld(org.bukkit.World world) {

	}

	public List<Furnace> loadTable() {
		return null;
	}
}
