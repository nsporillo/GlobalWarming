package net.porillo.effect.api.change;

public interface EffectChange<ChangeType> {

    //TODO: Do more useful stuff with this interface
    // Perhaps we might want to consider logging all atomic changes
    // and permitting the ability to rollback changes
    public ChangeType getType();
}
