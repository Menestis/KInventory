package fr.blendman974.kinventory.event;

import fr.blendman974.kinventory.inventories.KItem;

import java.beans.PropertyChangeEvent;

/**
 * @author Blendman974
 */
public class ContainerItemUpdateEvent extends PropertyChangeEvent {
    private final KItem source;

    public ContainerItemUpdateEvent(KItem source, String propertyName, Object oldValue, Object newValue) {
        super(source, propertyName, oldValue, newValue);
        this.source = source;
    }

    @Override
    public KItem getSource() {
        return this.source;
    }
}
