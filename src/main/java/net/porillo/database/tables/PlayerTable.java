package net.porillo.database.tables;

import lombok.Getter;
import net.porillo.objects.Player;
import net.porillo.objects.World;

import java.util.*;

public class PlayerTable extends Table {

	@Getter  private Map<UUID, Player> players = new HashMap<>();

	public PlayerTable() {
		super("players");
	}

	@Override
	public void createIfNotExists() {

	}

	public List<Player> loadTable() {
		return null;
	}
}
