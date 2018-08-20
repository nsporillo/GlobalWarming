package net.porillo.database.tables;

import lombok.Getter;
import net.porillo.objects.Furnace;
import net.porillo.objects.Player;

import java.util.*;

public class PlayerFurnaceTable extends Table {

	@Getter  private Map<Player, List<Furnace>> playerFurnaceMap = new HashMap<>();

	public PlayerFurnaceTable() {
		super("playerFurnaces");
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
