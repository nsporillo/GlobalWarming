package net.porillo.database.tables;

import lombok.Getter;
import net.porillo.objects.GPlayer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PlayerTable extends Table {

	@Getter
	private Map<UUID, GPlayer> players = new HashMap<>();

	public PlayerTable() {
		super("players");
	}

	@Override
	public void createIfNotExists() {

	}

	public List<GPlayer> loadTable() {
		return null;
	}
}
