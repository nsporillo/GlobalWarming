package net.porillo.database.api.select;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.ResultSet;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SelectionResult {

	private String tableName;
	private ResultSet resultSet;
	private UUID uuid;
}
