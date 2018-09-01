package net.porillo.objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.Location;

import java.sql.ResultSet;
import java.sql.SQLException;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tree {

	//TODO: Add JavaDocs
	private Integer uniqueID;
	private GPlayer owner;
	private Location location;
	private boolean isSapling;
	private Integer size;

	public Tree(ResultSet rs) throws SQLException {
		this.uniqueID = rs.getInt(1);
		//TODO Figure out a good way to handle these objects
		//While still being testable with testng

		this.isSapling = rs.getBoolean(7);
		this.size = rs.getInt(8);
	}
}
