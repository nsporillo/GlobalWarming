package net.porillo.database.tables;

import lombok.Getter;
import net.porillo.objects.Player;
import net.porillo.objects.World;

import java.util.ArrayList;
import java.util.List;

public class PlayerTable extends Table {

	@Getter  private List<Player> players = new ArrayList<>();

	public PlayerTable() {
		super("players");
	}

	@Override
	public void createIfNotExists() {

	}

	@Override
	public List<Player> loadTable() {
		return null;
	}
}
