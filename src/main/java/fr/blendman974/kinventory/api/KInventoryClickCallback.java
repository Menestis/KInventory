package fr.blendman974.kinventory.api;

import fr.blendman974.kinventory.inventories.KInventory;
import fr.blendman974.kinventory.inventories.KInventoryRepresentation;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * @author Blendman974
 */
@FunctionalInterface
public interface KInventoryClickCallback {
    void onClick(KInventoryRepresentation kInventory, ItemStack item, Player player, KInventoryClickContext clickContext);
}
