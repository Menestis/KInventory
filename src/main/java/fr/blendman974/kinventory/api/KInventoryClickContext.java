package fr.blendman974.kinventory.api;

import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;

/**
 * @author Blendman974
 */
public class KInventoryClickContext {
    private final InventoryAction inventoryAction;
    private final ClickType clickType;

    public KInventoryClickContext(InventoryAction inventoryAction, ClickType clickType) {
        this.inventoryAction = inventoryAction;
        this.clickType = clickType;
    }

    public InventoryAction getInventoryAction() {
        return inventoryAction;
    }

    public ClickType getClickType() {
        return clickType;
    }
}
