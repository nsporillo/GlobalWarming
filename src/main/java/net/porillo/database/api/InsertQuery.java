package net.porillo.database.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public abstract class InsertQuery implements Query {

    @Getter
    private String table;
}
