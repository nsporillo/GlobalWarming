package net.porillo.objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.porillo.GlobalWarming;
import net.porillo.database.tables.PlayerTable;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tree extends TrackedBlock {

    //TODO: Add JavaDocs
    private boolean isSapling;
    private Integer size;

    public Tree(Integer uniqueId, Integer ownerId, Location location, boolean isSapling, Integer size) {
        super(uniqueId, ownerId, location);
        this.isSapling = isSapling;
        this.size = size;
    }

    public Tree(ResultSet rs) throws SQLException {
        super(rs.getInt(1),
                rs.getInt(2),
                Bukkit.getWorld(UUID.fromString(rs.getString(3)))
                        .getBlockAt(rs.getInt(4), rs.getInt(5), rs.getInt(6)).getLocation());

        this.isSapling = rs.getBoolean(7);
        this.size = rs.getInt(8);
    }

    public GPlayer getOwner() {
        PlayerTable playerTable = GlobalWarming.getInstance().getTableManager().getPlayerTable();
        UUID ownerUUID = playerTable.getUuidMap().get(getOwnerId());
        return playerTable.getPlayers().get(ownerUUID);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Tree tree = (Tree) o;
        return getUniqueId().equals(tree.getUniqueId());
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + getUniqueId().hashCode();
        return result;
    }
}
