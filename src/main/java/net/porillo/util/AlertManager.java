package net.porillo.util;

import net.porillo.objects.GPlayer;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class AlertManager {

    private static AlertManager instance; // single instance

    private Set<UUID> subscribers = new HashSet<>();

    public void subscribe(UUID uuid) {
        this.subscribers.add(uuid);
    }

    public void unsubscribe(UUID uuid) {
        this.subscribers.remove(uuid);
    }

    public boolean isSubscribed(UUID uuid) {
        return subscribers.contains(uuid);
    }

    public void alert(GPlayer player, String message) {
        if (player == null) return;
        if (subscribers.contains(player.getUuid())) {
            player.sendMsg(message);
        }
    }

    public static AlertManager getInstance() {
        if (instance == null) instance = new AlertManager();
        return instance;
    }
}
