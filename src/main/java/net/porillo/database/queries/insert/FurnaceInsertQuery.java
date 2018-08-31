package net.porillo.database.queries.insert;

import net.porillo.database.api.InsertQuery;
import net.porillo.objects.Furnace;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class FurnaceInsertQuery extends InsertQuery {

	private Furnace furnace;

	public FurnaceInsertQuery(Furnace furnace) {
		super("furnaces");
		this.furnace = furnace;
	}

	@Override
	public String getSQL() {
		return "INSERT INTO furnaces (uniqueID, ownerUUID, worldName, blockX, blockY, blockZ, active) " +
				"VALUES (?,?,?,?,?,?,?)";
	}

	@Override
	public PreparedStatement prepareStatement(Connection connection) throws SQLException {
		PreparedStatement preparedStatement = connection.prepareStatement(getSQL());
		preparedStatement.setLong(1, furnace.getUniqueID());
		preparedStatement.setLong(2, furnace.getOwner().getUniqueId());
		preparedStatement.setString(3, furnace.getLocation().getWorld().getName());
		preparedStatement.setInt(4, furnace.getLocation().getBlockX());
		preparedStatement.setInt(5, furnace.getLocation().getBlockY());
		preparedStatement.setInt(6, furnace.getLocation().getBlockZ());
		preparedStatement.setBoolean(7, furnace.isActive());
		return preparedStatement;
	}

	@Override
	public Long getUniqueID() {
		return furnace.getUniqueID();
	}
}
