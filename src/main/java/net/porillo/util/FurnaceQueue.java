package net.porillo.util;

import net.porillo.objects.PlayerFurnaceFuel;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

public class FurnaceQueue {

    private final LinkedList<PlayerFurnaceFuel> furnaceQueue;
    private Map<UUID, PlayerFurnaceFuel> playerFurnaceFuelMap;

    public FurnaceQueue() {
        this.furnaceQueue = new LinkedList<>();
        this.playerFurnaceFuelMap = new HashMap<>();
    }

    public boolean hasFuelForPlayer(UUID playerId) {
        return playerFurnaceFuelMap.containsKey(playerId);
    }

    public PlayerFurnaceFuel getPlayerFurnaceFuel(UUID uuid) {
        return playerFurnaceFuelMap.get(uuid);
    }

    public PlayerFurnaceFuel getFirstInsertedFuel() {
        return furnaceQueue.pollFirst();
    }

    public void burnFuel(ItemStack itemStack) {
        PlayerFurnaceFuel firstInsertedFuel = getFirstInsertedFuel();

    }

    public void insertFuel(UUID playerId, ItemStack itemStack) {
        if (hasFuelForPlayer(playerId)) {
            PlayerFurnaceFuel playerFurnaceFuel = getPlayerFurnaceFuel(playerId);
            furnaceQueue.remove(playerFurnaceFuel);
            PlayerFurnaceFuel newFuel = PlayerFurnaceFuel.builder()
                    .playerId(playerId)
                    .fuelType(itemStack.getType())
                    .count(playerFurnaceFuel.getCount() + itemStack.getAmount())
                    .build();
            furnaceQueue.add(newFuel);
            playerFurnaceFuelMap.put(playerId, newFuel);
        } else {
            PlayerFurnaceFuel newFuel = PlayerFurnaceFuel.builder()
                    .playerId(playerId)
                    .fuelType(itemStack.getType())
                    .count(itemStack.getAmount())
                    .build();
            furnaceQueue.add(newFuel);
            playerFurnaceFuelMap.put(playerId, newFuel);
        }
    }

    public void removeFuel(UUID playerId, ItemStack itemStack) {
        int removedAmount = 0;
        final int toRemove = itemStack.getAmount();
        while (removedAmount < toRemove) {
            // if a player removes fuel, remove theirs first and then remove others
            if (hasFuelForPlayer(playerId)) {
                PlayerFurnaceFuel playerFurnaceFuel = getPlayerFurnaceFuel(playerId);
                removedAmount = playerFurnaceFuel.getCount();
                furnaceQueue.remove(playerFurnaceFuel);
                playerFurnaceFuelMap.remove(playerId);

                if (removedAmount >= toRemove) {
                    break;
                }

                PlayerFurnaceFuel newFuel = PlayerFurnaceFuel.builder()
                        .playerId(playerId)
                        .fuelType(itemStack.getType())
                        .count(playerFurnaceFuel.getCount() - itemStack.getAmount())
                        .build();
                furnaceQueue.add(newFuel);
                playerFurnaceFuelMap.put(playerId, newFuel);
            } else {
                Map<UUID, PlayerFurnaceFuel> newFurnaceMap = new HashMap<>(playerFurnaceFuelMap);
                for (Map.Entry<UUID, PlayerFurnaceFuel> entry : playerFurnaceFuelMap.entrySet()) {
                    removedAmount = entry.getValue().getCount();
                    playerFurnaceFuelMap.remove(playerId);
                    newFurnaceMap.remove(entry.getKey());

                    if (removedAmount >= toRemove) {
                        break;
                    }

                    PlayerFurnaceFuel newFuel = PlayerFurnaceFuel.builder()
                            .playerId(playerId)
                            .fuelType(itemStack.getType())
                            .count(entry.getValue().getCount() - itemStack.getAmount())
                            .build();
                    furnaceQueue.add(newFuel);
                    newFurnaceMap.put(playerId, newFuel);
                }

                this.playerFurnaceFuelMap = newFurnaceMap;
            }
        }
    }
}
