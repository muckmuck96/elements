package de.muckmuck96.elements.element.menu;

import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;

/**
 * Event fired when a player clicks in a BaseMenu.
 */
public class MenuClickEvent extends InventoryClickEvent {
    private static final HandlerList handlerList = new HandlerList();

    public MenuClickEvent(@NotNull InventoryView view, InventoryType.@NotNull SlotType type, int slot, @NotNull ClickType click, @NotNull InventoryAction action) {
        super(view, type, slot, click, action);
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
}
