package fr.blendman974.kinventory.inventories;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.*;

/**
 * @author Blendman974
 */
public class KInventoryService {
    private final Map<UUID, LinkedList<KInventoryRepresentation>> history = new HashMap<>();

    public Optional<KInventoryRepresentation> getCurrentInventory(UUID uuid) {
        return Optional.ofNullable(this.history.get(uuid)).map(LinkedList::getLast);
    }

    void setCurrentInventory(UUID uuid, KInventoryRepresentation representation) {
        LinkedList<KInventoryRepresentation> list = this.history.getOrDefault(uuid, new LinkedList<>());
        list.addLast(representation);
        this.history.put(uuid, list);
    }

    public LinkedList<KInventoryRepresentation> getHistory(Player user) {
        return new LinkedList<>(this.history.getOrDefault(user.getUniqueId(), new LinkedList<>()));
    }

    public void clearHistory(UUID uuid) {
        history.remove(uuid);
    }

}
