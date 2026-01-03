package de.muckmuck96.elements.element.button.events;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ButtonDoNothingEvent implements ButtonClickEvent {
    public static final ButtonClickEvent INSTANCE = new ButtonDoNothingEvent();

    @Override
    public void onClick(Inventory inventory, Player player, ItemStack itemStack, ClickType click) {
        // do nothing
    }
}
