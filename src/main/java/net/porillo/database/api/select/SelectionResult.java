package net.porillo.database.api.select;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.ResultSet;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SelectionResult {

	private String tableName;
	private ResultSet resultSet;
}
