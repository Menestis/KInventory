package fr.blendman974.kinventory.inventories;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author Blendman974
 */
public interface ContainerElement {
    ItemStack constructItem(Player player);
}
