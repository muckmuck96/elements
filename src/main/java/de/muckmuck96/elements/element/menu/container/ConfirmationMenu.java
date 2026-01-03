package de.muckmuck96.elements.element.menu.container;

import de.muckmuck96.elements.element.button.ItemBuilder;
import de.muckmuck96.elements.element.menu.BaseMenu;
import de.muckmuck96.elements.element.menu.MenuClickEvent;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Simple yes/no confirmation dialog menu.
 */
public class ConfirmationMenu extends BaseMenu {
    private ItemStack midItem;
    
    private ItemStack confirmItem = new ItemBuilder(Material.GREEN_WOOL).name("§aBestätigen").build();
    private ItemStack cancelItem = new ItemBuilder(Material.RED_WOOL).name("§cAbbrechen").build();
    private List<String> confirmLore = new ArrayList<>();
    private List<String> cancelLore = new ArrayList<>();

    public ConfirmationMenu(Plugin plugin, String name, ItemStack midItem, List<String> confirmLore, List<String> cancelLore) {
        super(name, plugin);
        this.midItem = midItem;
        this.confirmLore = confirmLore;
        this.cancelLore = cancelLore;
    }

    public ConfirmationMenu(Plugin plugin, String name, ItemStack midItem, List<String> confirmLore) {
        super(name, plugin);
        this.midItem = midItem;
        this.confirmLore = confirmLore;
    }

    public ConfirmationMenu(Plugin plugin, String name, ItemStack midItem) {
        super(name, plugin);
        this.midItem = midItem;
    }

    public ConfirmationMenu(Plugin plugin, String name, String confirmItemName, String cancelItemName, ItemStack midItem) {
        super(name, plugin);
        this.midItem = midItem;
        setConfirmItem(new ItemBuilder(confirmItem).name(confirmItemName).build());
        setCancelItem(new ItemBuilder(cancelItem).name(cancelItemName).build());
    }

    protected Function<MenuClickEvent, Void> executeOnConfirmation() {
        return event -> null;
    }

    protected Function<MenuClickEvent, Void> executeOnCancellation() {
        return event -> null;
    }

    public ItemStack getMidItem() {
        return midItem;
    }

    public void setMidItem(ItemStack midItem) {
        this.midItem = midItem;
    }

    public ItemStack getConfirmItem() {
        return confirmItem;
    }

    public void setConfirmItem(ItemStack confirmItem) {
        this.confirmItem = confirmItem;
    }

    public ItemStack getCancelItem() {
        return cancelItem;
    }

    public void setCancelItem(ItemStack cancelItem) {
        this.cancelItem = cancelItem;
    }

    private void init() {
        if (!confirmLore.isEmpty())
            this.confirmItem = new ItemBuilder(confirmItem.clone()).lore(confirmLore).build();
        if (!cancelLore.isEmpty())
            this.cancelItem = new ItemBuilder(cancelItem.clone()).lore(cancelLore).build();

        addItem(confirmItem, 0);
        addItem(midItem, 2);
        addItem(cancelItem, 4);
    }

    @Override
    public void open(Player player) {
        init();
        super.open(player);
    }

    @Override
    public void dispose() {
        midItem = null;
        confirmItem = null;
        cancelItem = null;
        confirmLore = null;
        cancelLore = null;
        super.dispose();
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerPickupItemBlock(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;
        boolean watching = getInventory().getViewers().stream()
                .map(Entity::getUniqueId)
                .anyMatch(uuid -> uuid.equals(event.getEntity().getUniqueId()));
        if (watching)
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerPickupItemBlock(InventoryClickEvent event) {
        if (getInventory() == null)
            return;

        if (event.getClickedInventory() == null)
            return;
        boolean watching = getInventory().getViewers().stream()
                .map(Entity::getUniqueId)
                .anyMatch(uuid -> uuid.equals(event.getWhoClicked().getUniqueId()));
        if (watching)
            event.setCancelled(true);
    }

    @EventHandler
    public void onMenuClick(MenuClickEvent event) {
        if (getInventory() == null || !event.getInventory().equals(getInventory())) {
            return;
        }

        ItemStack clicked = event.getCurrentItem();
        // Store local references to avoid race condition
        ItemStack confirm = this.confirmItem;
        ItemStack cancel = this.cancelItem;

        if (clicked == null || confirm == null || cancel == null) {
            return;
        }
        if (confirm.isSimilar(clicked)) {
            Function<MenuClickEvent, Void> onConfirm = executeOnConfirmation();
            if (onConfirm != null) {
                onConfirm.apply(event);
            }
        } else if (cancel.isSimilar(clicked)) {
            Function<MenuClickEvent, Void> onCancel = executeOnCancellation();
            if (onCancel != null) {
                onCancel.apply(event);
            }
        }
    }

}
