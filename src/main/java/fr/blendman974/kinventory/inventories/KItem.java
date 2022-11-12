package fr.blendman974.kinventory.inventories;

import fr.blendman974.kinventory.api.KInventoryClickCallback;
import fr.blendman974.kinventory.event.ContainerItemUpdateEvent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * @author Blendman974
 */
public class KItem implements ContainerElement {

    public static final KItem DEFAULT = new KItem(player -> new ItemStack(Material.BARRIER));

    private final PropertyChangeSupport eventPoll;
    private Function<Player, ItemStack> item;
    private Function<Player, String> nameFunction = null;
    private Function<Player, List<String>> descriptionFunction = null;
    private final List<KInventoryClickCallback> clickCallbacks = new ArrayList<>();

    public KItem(Function<Player, ItemStack> item) {
        this.eventPoll = new PropertyChangeSupport(this);
        this.item = Objects.requireNonNull(item);
    }

    public KItem(ItemStack item) {
        this.eventPoll = new PropertyChangeSupport(this);
        this.item = player -> Objects.requireNonNull(item);
    }

    /***
     * Returns the support attached to this item
     *
     * @return The attached support
     */
    PropertyChangeSupport getEventPoll() {
        return eventPoll;
    }

    /**
     * Returns the in-game item
     *
     * @return The in-game item
     */
    public Function<Player, ItemStack> getItem() {
        return item;
    }

    /**
     * Set the in-game item
     *
     * @param item The in-game item
     */
    public void setItem(Function<Player, ItemStack> item) {
        Function<Player, ItemStack> prevItem = this.item;
        this.item = Objects.requireNonNull(item);
        this.eventPoll.firePropertyChange(new ContainerItemUpdateEvent(this, "item", prevItem, this.item));
    }

    /**
     * Set the in-game item
     *
     * @param item The in-game item
     */
    public void setItem(ItemStack item) {
        Function<Player, ItemStack> prevItem = this.item;
        this.item = player -> Objects.requireNonNull(item);
        this.eventPoll.firePropertyChange(new ContainerItemUpdateEvent(this, "item", prevItem, this.item));
    }

    /**
     * Returns the function to obtains the item name
     *
     * @return The function to have the name, or {@code null} to use the item display name
     */
    public Function<Player, String> getName() {
        return nameFunction;
    }

    /**
     * Set the function to obtains the item name
     *
     * @param nameFunction The function or {@code null} to use the item display name
     */
    public void setName(Function<Player, String> nameFunction) {
        Function<Player, String> prevNameFunction = this.nameFunction;
        this.nameFunction = nameFunction;
        this.eventPoll.firePropertyChange(new ContainerItemUpdateEvent(this, "name", prevNameFunction, this.nameFunction));
    }

    /**
     * Set the function to obtains the item name
     *
     * @param name The name to set, {@code null} is not allowed
     */
    public void setName(String name) {
        setName(a -> Objects.requireNonNull(name));
    }

    /**
     * Returns the function to obtains the item lore
     *
     * @return The function to have the item lore, or {@code null} to use item lore
     */
    public Function<Player, List<String>> getDescription() {
        return descriptionFunction;
    }

    /**
     * Set the function to obtains the item lore
     *
     * @param descriptionFunction The function or {@code null} to use the lore
     */
    public void setDescription(Function<Player, List<String>> descriptionFunction) {
        Function<Player, List<String>> prevDescriptionFunction = this.descriptionFunction;
        this.descriptionFunction = descriptionFunction;
        this.eventPoll.firePropertyChange(new ContainerItemUpdateEvent(this, "description", prevDescriptionFunction, this.descriptionFunction));
    }

    /**
     * Set the function to obtains the item lore
     *
     * @param description The function to set, {@code null} is not allowed
     */
    public void setDescription(List<String> description) {
        setDescription(a -> Objects.requireNonNull(description));
    }

    @Override
    public ItemStack constructItem(Player player) {
        ItemStack apply = getItem().apply(player);
        if (apply == null) {
            return null;
        }
        ItemStack item = apply.clone();
        ItemMeta meta = Objects.requireNonNull(item.getItemMeta());
        if (nameFunction != null) {
            meta.setDisplayName(getName().apply(player));
        }
        if (descriptionFunction != null) {
            meta.setLore(getDescription().apply(player));
        }
        item.setItemMeta(meta);
        return item;
    }

    /**
     * Get all events bind on this item
     *
     * @return A collection of all events
     */
    public List<KInventoryClickCallback> getCallbacks() {
        return Collections.unmodifiableList(clickCallbacks);
    }

    /**
     * Clear all events bind on this item
     */
    public void clearCallbacks() {
        this.clickCallbacks.clear();
    }

    /**
     * Add a event on this item
     *
     * @param callback The event to add
     */
    public void addCallback(KInventoryClickCallback callback) {
        this.clickCallbacks.add(callback);
    }

    /**
     * Remove a event from this item
     *
     * @param callback The event to remove
     */
    public void removeCallback(KInventoryClickCallback callback) {
        this.clickCallbacks.remove(callback);
    }

}
