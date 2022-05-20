package fr.blendman974.kinventory.event;

import fr.blendman974.kinventory.inventories.ContainerElement;
import fr.blendman974.kinventory.inventories.KInventory;

import java.beans.PropertyChangeEvent;

/**
 * @author Blendman974
 */
public class ContainerInventoryElementChangeEvent extends PropertyChangeEvent {

    private final KInventory source;
    private final ContainerElement oldValue, newValue;

    public ContainerInventoryElementChangeEvent(KInventory source, ContainerElement oldValue, ContainerElement newValue) {
        super(source, "element", oldValue, newValue);
        this.source = source;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    @Override
    public KInventory getSource() {
        return this.source;
    }

    @Override
    public ContainerElement getOldValue() {
        return this.oldValue;
    }

    @Override
    public ContainerElement getNewValue() {
        return this.newValue;
    }
}

