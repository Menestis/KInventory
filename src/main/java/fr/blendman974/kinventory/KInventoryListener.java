package fr.blendman974.kinventory;

import fr.blendman974.kinventory.api.KInventoryClickContext;
import fr.blendman974.kinventory.inventories.*;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * @author Blendman974
 */
public class KInventoryListener implements Listener {
    private final static Set<UUID> persistentInventory = new HashSet<>();
    private final KInventoryService service;

    public KInventoryListener(KInventoryService service) {
        this.service = service;
    }

    private static void destroy(KInventoryRepresentation representation, Player player) {
        final Consumer<Player> closeConsumer = representation.getInventory().getCloseConsumer();
        if (closeConsumer != null)
            closeConsumer.accept(player);

        // This representation can be destroyed
        representation.destroy();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();

        // The player is only interacting with his own inventory
        if (event.getInventory() instanceof PlayerInventory && event.getClickedInventory() instanceof PlayerInventory)
            return;

        // The player is not on any GUI
        Optional<KInventoryRepresentation> inventory = this.service.getCurrentInventory(player.getUniqueId());
        if (!inventory.isPresent())
            return;

        // The action seems to be a drag and drop action, disable-it
        if (!event.getInventory().equals(event.getClickedInventory())) {
            event.setCancelled(true);
            return;
        }

        // No item is present here, ignore
        if (event.getCurrentItem() == null || event.getCurrentItem().getType().equals(Material.AIR))
            return;

        ContainerElement element = inventory.get().getInventory().getContent().get(event.getSlot());

        // This slot seems to have an item, but the item is not properly register
        if (element == null) {
            event.setCancelled(true);
            throw new UnsupportedOperationException(String.format("Inventory %s seems to have an item not registered", event.getView().getTitle()));
        }

        event.setCancelled(true);

        // Finally dispatch events
        if (element instanceof KItem) {
            KInventoryClickContext clickContext = new KInventoryClickContext(event.getAction(), event.getClick());
            KItem kItem = (KItem) element;
            kItem.getCallbacks()
                    .forEach(callback -> callback.onClick(inventory.get(), event.getCurrentItem(), player, clickContext));
        } else if (element instanceof KInventory) {
            persistentInventory.add(player.getUniqueId());
            ((KInventory) element).open(player);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;
        Player player = (Player) event.getPlayer();

        final Optional<KInventoryRepresentation> currentInventory = service.getCurrentInventory(player.getUniqueId());

        if (!currentInventory.isPresent())
            return;

        if (!persistentInventory.remove(player.getUniqueId())) {
            this.service.clearHistory(player.getUniqueId());
        }
        destroy(currentInventory.get(), player);

    }

}
