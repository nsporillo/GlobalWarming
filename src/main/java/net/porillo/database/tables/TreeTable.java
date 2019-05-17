package net.porillo.database.tables;

import net.porillo.GlobalWarming;
import net.porillo.database.api.SelectCallback;
import net.porillo.database.queries.delete.TreeDeleteQuery;
import net.porillo.database.queries.select.TreeSelectQuery;
import net.porillo.database.queue.AsyncDBQueue;
import net.porillo.objects.Tree;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class TreeTable extends TrackedBlockTable implements SelectCallback<Tree> {
    public TreeTable() {
        super("trees");
        createIfNotExists();
        TreeSelectQuery selectQuery = new TreeSelectQuery(this);
        AsyncDBQueue.getInstance().queueSelectQuery(selectQuery);
    }

    @Override
    public void onSelectionCompletion(List<Tree> returnList) {
        if (GlobalWarming.getInstance() != null) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    for (Tree tree : returnList) {
                        updateCollections(tree);
                    }
                }
            }.runTask(GlobalWarming.getInstance());
        } else {
            System.out.printf("Selection returned %d trees%n", returnList.size());
        }
    }

    @Override
    public Tree deleteLocation(Location location) {
        Tree deletedTree = (Tree) super.deleteLocation(location);
        if (deletedTree != null) {
            TreeDeleteQuery deleteQuery = new TreeDeleteQuery(deletedTree);
            AsyncDBQueue.getInstance().queueDeleteQuery(deleteQuery);
        }

        return deletedTree;
    }
}
