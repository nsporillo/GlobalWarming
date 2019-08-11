package net.porillo.database.queries.select;

import net.porillo.database.api.SelectQuery;
import net.porillo.database.tables.FurnaceTable;
import net.porillo.objects.Furnace;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class FurnaceSelectQuery extends SelectQuery<Furnace, FurnaceTable> {

    public FurnaceSelectQuery(FurnaceTable callback) {
        super("furnaces", callback);
    }

    @Override
    public List<Furnace> queryDatabase(Connection connection) throws SQLException {
        List<Furnace> furnaces = new ArrayList<>();
        ResultSet rs = prepareStatement(connection).executeQuery(getSQL());

        while (rs.next()) {
            furnaces.add(new Furnace(rs));
        }

        return furnaces;
    }

    @Override
    public Statement prepareStatement(Connection connection) throws SQLException {
        return connection.createStatement(); // H2 doesn't allow empty prepared statements
    }

    @Override
    public String getSQL() {
        return "SELECT * FROM furnaces WHERE active = true";
    }
}
