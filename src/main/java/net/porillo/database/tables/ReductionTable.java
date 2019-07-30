package net.porillo.database.tables;

import lombok.Getter;
import net.porillo.objects.Reduction;

import java.util.ArrayList;
import java.util.List;

public class ReductionTable extends Table {

    @Getter
    private List<Reduction> reductions = new ArrayList<>();

    public ReductionTable() {
        super("reductions");
        createIfNotExists();
    }
}
