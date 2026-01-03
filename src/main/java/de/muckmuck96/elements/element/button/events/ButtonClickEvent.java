package de.muckmuck96.elements.element.button.events;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Handler interface for button click events.
 */
public interface ButtonClickEvent {
    void onClick(Inventory inventory, Player player, ItemStack itemStack, ClickType click);
}
