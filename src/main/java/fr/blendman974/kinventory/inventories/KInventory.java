package fr.blendman974.kinventory.inventories;

import fr.blendman974.kinventory.KInventoryManager;
import fr.blendman974.kinventory.event.ContainerInventoryElementChangeEvent;
import fr.blendman974.kinventory.event.ContainerInventoryUpdateEvent;
import fr.blendman974.kinventory.event.ContainerItemUpdateEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import java.beans.PropertyChangeSupport;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author Blendman974
 */
public class KInventory implements ContainerElement {

    private final Map<Integer, ContainerElement> content = new HashMap<>();
    private final PropertyChangeSupport containerInventorySupport;
    private final PropertyChangeSupport itemEventPoll;
    private final List<KInventoryRepresentation> representations = new ArrayList<>();
    private Consumer<Player> closeConsumer = null;
    private KItem itemRepresentation;
    private Function<Player, String> nameFunction;

    private KInventoryType type;

    public KInventory(KItem item, KInventoryType type, Function<Player, String> nameFunction) {
        this.itemEventPoll = new PropertyChangeSupport(this);
        this.containerInventorySupport = new PropertyChangeSupport(this);
        this.itemRepresentation = Objects.requireNonNull(item);
        this.setName(nameFunction);
        this.type = Objects.requireNonNull(type);
    }

    public KInventory(KInventoryType type, Function<Player, String> nameFunction) {
        this(KItem.DEFAULT, type, nameFunction);
    }

    public KInventory(KItem item, KInventoryType type, String name) {
        this(item, type, player -> name);
    }

    public KInventory(KInventoryType type, String name) {
        this(KItem.DEFAULT, type, player -> name);
    }

    public KInventory(KItem item, InventoryType type, Function<Player, String> nameFunction) {
        this(item, new KInventoryType(type), nameFunction);
    }

    public KInventory(InventoryType type, Function<Player, String> nameFunction) {
        this(KItem.DEFAULT, new KInventoryType(type), nameFunction);
    }

    public KInventory(KItem item, InventoryType type, String name) {
        this(item, new KInventoryType(type), player -> name);
    }

    public KInventory(InventoryType type, String name) {
        this(KItem.DEFAULT, new KInventoryType(type), player -> name);
    }


    public KInventory(KItem item, int size, Function<Player, String> nameFunction) {
        this(item, new KInventoryType(size), nameFunction);
    }

    public KInventory(int size, Function<Player, String> nameFunction) {
        this(KItem.DEFAULT, new KInventoryType(size), nameFunction);
    }

    public KInventory(KItem item, int size, String name) {
        this(item, new KInventoryType(size), player -> name);
    }

    public KInventory(int size, String name) {
        this(KItem.DEFAULT, new KInventoryType(size), player -> name);
    }


    /**
     * Returns a map of all elements with the slot as a key
     *
     * @return A immutable map containing all elements
     */
    public Map<Integer, ContainerElement> getContent() {
        return Collections.unmodifiableMap(this.content);
    }

    /**
     * Returns the close consumer on this inventory
     *
     * @return The close consumer, can be {@literal null}
     */
    public Consumer<Player> getCloseConsumer() {
        return closeConsumer;
    }

    /**
     * Set the close consumer on this inventory
     *
     * @param closeConsumer The close consumer, can be {@literal null}
     */
    public void setCloseConsumer(Consumer<Player> closeConsumer) {
        this.closeConsumer = closeConsumer;
    }

    /**
     * Returns the itemRepresentation who represent this inventory
     *
     * @return The itemRepresentation who represent this inventory
     */
    public KItem getItemRepresentation() {
        return itemRepresentation;
    }

    /**
     * Set the itemRepresentation who represent this inventory
     *
     * @param itemRepresentation The itemRepresentation representing this inventory, events are allowed. {@code null} values are not allowed.
     */
    public void setItemRepresentation(KItem itemRepresentation) {
        KItem prevItem = this.itemRepresentation;
        this.itemRepresentation = Objects.requireNonNull(itemRepresentation);
        this.itemEventPoll.firePropertyChange(new ContainerInventoryUpdateEvent(this, "itemRepresentation", prevItem, this.itemRepresentation));
    }

    /**
     * Returns the name of this inventory
     *
     * @return The name of this inventory, never {@code null}
     */
    public Function<Player, String> getName() {
        return nameFunction;
    }

    /**
     * Set the name of this inventory
     *
     * @param nameFunction The function to obtain the name of this inventory
     * @throws NullPointerException If the provided function is {@code null}
     */
    public void setName(Function<Player, String> nameFunction) {
        this.nameFunction = Objects.requireNonNull(nameFunction);
    }

    /**
     * Set the name of this inventory
     *
     * @param name The name of this inventory
     * @throws NullPointerException If the provided function is {@code null}
     */
    public final void setName(String name) {
        Objects.requireNonNull(name);
        this.setName(p -> name);
    }

    /**
     * Returns the size of this inventory
     *
     * @return The inventory size
     */
    public KInventoryType getType() {
        return type;
    }


    /**
     * Returns the size of this inventory
     *
     * @return The inventory size
     */
    public int getSize() {
        return getType().isSize() ? getType().getSize() : getType().getType().getDefaultSize();
    }

    /**
     * Set the size of this inventory
     *
     * @param type The inventory size
     */
    public void setType(KInventoryType type) {
        this.type = type;
    }

    /**
     * Set the size of this inventory
     *
     * @param type The inventory size
     */
    public void setType(InventoryType type) {
        this.type = new KInventoryType(type);
    }

    /**
     * Set the size of this inventory
     *
     * @param size The inventory size
     * @throws IllegalArgumentException If the size is not a multiple of 9
     */
    public void setSize(int size) {
        this.type = new KInventoryType(size);
    }


    /**
     * Set an element on a specific slot
     *
     * @param slot    The slot
     * @param element The element to set, can be {@code null} if this slot should be empty
     * @throws IndexOutOfBoundsException If the slot is negative or outside of the inventory
     */
    public void setElement(int slot, ContainerElement element) {
        ContainerElement previousValue;
        if (element == null)
            previousValue = this.content.remove(slot);
        else
            previousValue = this.content.put(slot, element);
        this.containerInventorySupport.firePropertyChange(new ContainerInventoryElementChangeEvent(this, previousValue, element));

        if (element == null) {
            // Want to clear the slot
            setElementListener(slot, p -> null);
        } else if (element instanceof KItem) {
            // The item that was set
            KItem itemElement = ((KItem) element);
            // For all representation, rebuild this item and set-it
            setElementListener(slot, itemElement::constructItem);
            // Track changes on this item
            itemElement.getEventPoll().addPropertyChangeListener(event -> {
                if (event instanceof ContainerItemUpdateEvent) {
                    KItem itemSource = ((ContainerItemUpdateEvent) event).getSource();
                    setElementListener(slot, itemSource::constructItem);
                }
            });
        } else if (element instanceof KInventory) {
            // The item (inventory) that was set
            KInventory inventoryElement = (KInventory) element;
            // For all representation, rebuild the item and set-it
            setElementListener(slot, p -> inventoryElement.getItemRepresentation().constructItem(p));
            // Track changes on the item representing this inventory
            inventoryElement.getItemRepresentation().getEventPoll().addPropertyChangeListener(listener -> setElementListener(slot, p -> ((KItem) listener.getSource()).constructItem(p)));
            this.itemEventPoll.addPropertyChangeListener(listener -> setElementListener(slot, p -> ((KItem) listener.getNewValue()).constructItem(p)));
        }
    }

    public void removeElement(int slot) {
        ContainerElement previousValue = this.content.remove(slot);
        this.containerInventorySupport.firePropertyChange(new ContainerInventoryElementChangeEvent(this, previousValue, null));

        // Want to clear the slot
        setElementListener(slot, p -> null);
    }

    public void clear() {
        Set<Integer> i = new HashSet<>(this.content.keySet());
        i.forEach(this::removeElement);
    }

    protected final void trackRepresentation(KInventoryRepresentation representation) {
        this.representations.add(representation);
    }

    private void setElementListener(int slot, Function<Player, ItemStack> playerItemConsumer) {
        this.representations.forEach(representation ->
                representation.setElementListener(slot, playerItemConsumer.apply(representation.getPlayer())));
    }

    /**
     * Add an element on the next free slot
     *
     * @param element The element to add, can be {@code null} if this slot should be empty
     * @throws IndexOutOfBoundsException If this inventory do not have any free slot
     * @see #setElement(int, ContainerElement)
     */
    public final void addElement(ContainerElement element) throws IndexOutOfBoundsException {
        OptionalInt freeSlot = this.getFreeSlot();
        if (!freeSlot.isPresent()) throw new IndexOutOfBoundsException("No free slot on this inventory");
        this.setElement(freeSlot.getAsInt(), element);
    }

    /**
     * Returns the first free slot if this container if any
     *
     * @return The first free slot or {@link OptionalInt#empty()} if this container is full
     */
    public final OptionalInt getFreeSlot() {
        for (int i = 0; i < this.getSize(); ++i) {
            if (!this.content.containsKey(i))
                return OptionalInt.of(i);
        }
        return OptionalInt.empty();
    }

    public final void destroyRepresentation(KInventoryRepresentation representation) {
        this.representations.remove(representation);
    }

    public void open(Player player) {
        Bukkit.getScheduler().runTask(KInventoryManager.getPlugin(), () -> {
            KInventoryRepresentation representation = new KInventoryRepresentation(this, player);
            trackRepresentation(representation);
            representation.open();
        });
    }

    public KInventoryRepresentation getRepresentation(Player player) {
        KInventoryRepresentation representation = new KInventoryRepresentation(this, player);
        trackRepresentation(representation);
        return representation;
    }


    @Override
    public ItemStack constructItem(Player player) {
        return getItemRepresentation().constructItem(player);
    }
}
