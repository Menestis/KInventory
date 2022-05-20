package fr.blendman974.kinventory.event;

import fr.blendman974.kinventory.inventories.KInventory;

import java.beans.PropertyChangeEvent;

/**
 * @author Blendman974
 */
public class ContainerInventoryUpdateEvent extends PropertyChangeEvent {

    private final KInventory source;

    public ContainerInventoryUpdateEvent(KInventory source, String propertyName, Object oldValue, Object newValue) {
        super(source, propertyName, oldValue, newValue);
        this.source = source;
    }

    @Override
    public KInventory getSource() {
        return this.source;
    }
}
