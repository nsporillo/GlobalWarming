package net.porillo.database.queries.select;

import net.porillo.database.api.SelectQuery;
import net.porillo.database.tables.TreeTable;
import net.porillo.objects.Tree;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TreeSelectQuery extends SelectQuery<Tree, TreeTable> {

    public TreeSelectQuery(TreeTable callback) {
        super("trees", callback);
    }

    @Override
    public String getSQL() {
        return "SELECT * FROM trees WHERE sapling = true";
    }

    @Override
    public List<Tree> queryDatabase(Connection connection) throws SQLException {
        List<Tree> treeList = new ArrayList<>();
        ResultSet rs = prepareStatement(connection).executeQuery(getSQL());

        while (rs.next()) {
            treeList.add(new Tree(rs));
        }

        return treeList;
    }

    @Override
    public Statement prepareStatement(Connection connection) throws SQLException {
        return connection.createStatement(); // H2 doesn't allow empty prepared statements
    }
}
