package de.muckmuck96.elements.element.menu;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class MenuHolder implements InventoryHolder {
    private final BaseMenu menu;
    private Inventory inventory;

    public MenuHolder(BaseMenu menu) {
        this.menu = menu;
    }

    void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public BaseMenu getMenu() {
        return menu;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
