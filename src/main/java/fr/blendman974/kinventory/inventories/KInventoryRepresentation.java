package fr.blendman974.kinventory.inventories;

import fr.blendman974.kinventory.KInventoryManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * @author Blendman974
 */
public class KInventoryRepresentation {
    private final KInventory inventory;
    private final Player player;
    private final Inventory craftInventory;

    KInventoryRepresentation(KInventory inventory, Player player) {
        this.inventory = inventory;
        this.player = player;
        KInventoryType type = inventory.getType();
        if (type.isType()) {
            this.craftInventory = Bukkit.createInventory(null, type.getType(), inventory.getName().apply(player));
        } else {
            this.craftInventory = Bukkit.createInventory(null, type.getSize(), inventory.getName().apply(player));
        }
        inventory.getContent().forEach((slot, element) -> craftInventory.setItem(slot, element.constructItem(player)));
    }

    void setElementListener(int slot, ItemStack item) {
        if (item == null) craftInventory.clear(slot);
        else craftInventory.setItem(slot, item);
    }

    public void open() {
        player.openInventory(craftInventory);
        KInventoryManager.getService().setCurrentInventory(player.getUniqueId(), this);
    }

    public Inventory getCraftInventory() {
        return craftInventory;
    }

    public void destroy() {
        this.inventory.destroyRepresentation(this);
    }

    public Player getPlayer() {
        return player;
    }

    public KInventory getInventory() {
        return inventory;
    }
}
