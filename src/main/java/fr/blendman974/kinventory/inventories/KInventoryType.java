package fr.blendman974.kinventory.inventories;

import org.bukkit.event.inventory.InventoryType;

/**
 * @author Blendman974
 */
public class KInventoryType {
    private final InventoryType type;
    private final int size;

    public KInventoryType(InventoryType type) {
        this.type = type;
        this.size = 0;
    }

    public KInventoryType(int size) {
        if (size % 9 != 0) {
            throw new IllegalArgumentException("Size must be a multiple of 9 and not null");
        }
        this.size = size;
        this.type = null;
    }

    public boolean isType() {
        return type != null;
    }

    public boolean isSize() {
        return size != 0;
    }


    public InventoryType getType() {
        return type;
    }

    public int getSize() {
        return size;
    }

}
