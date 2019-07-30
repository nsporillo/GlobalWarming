package net.porillo.effect.api.change;

public enum ChangeType {

    /**
     * When an atomic change impacts only a single block
     */
    BLOCK,
    /**
     * When an atomic change impacts only a single entity
     */
    ENTITY,
    /**
     * When an atomic change impacts the entire world
     */
    GLOBAL
}
