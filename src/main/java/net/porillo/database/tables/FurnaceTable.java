package net.porillo.database.tables;

import net.porillo.GlobalWarming;
import net.porillo.database.api.SelectCallback;
import net.porillo.database.queries.delete.FurnaceDeleteQuery;
import net.porillo.database.queries.select.FurnaceSelectQuery;
import net.porillo.database.queue.AsyncDBQueue;
import net.porillo.objects.Furnace;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class FurnaceTable extends TrackedBlockTable implements SelectCallback<Furnace> {

    public FurnaceTable() {
        super("furnaces");
        createIfNotExists();

        FurnaceSelectQuery selectQuery = new FurnaceSelectQuery(this);
        AsyncDBQueue.getInstance().queueSelectQuery(selectQuery);
    }

    @Override
    public void onSelectionCompletion(List<Furnace> returnList) {
        if (GlobalWarming.getInstance() != null) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    for (Furnace furnace : returnList) {
                        updateCollections(furnace);
                    }
                }
            }.runTask(GlobalWarming.getInstance());
        } else {
            System.out.printf("Selection returned %d furnaces.%n", returnList.size());
        }
    }

    @Override
    public Furnace deleteLocation(Location location) {
        Furnace deletedFurnace = (Furnace) super.deleteLocation(location);
        if (deletedFurnace != null) {
            FurnaceDeleteQuery deleteQuery = new FurnaceDeleteQuery(deletedFurnace);
            AsyncDBQueue.getInstance().queueDeleteQuery(deleteQuery);
        }

        return deletedFurnace;
    }
}
