# Menus

Inventory-based GUIs with built-in event handling.

## Setup

```java
MenuRegistry menus = registry.enable(MenuRegistry.class);
```

## BaseMenu

Extend this abstract class to create custom menus:

```java
public class ShopMenu extends BaseMenu implements Listener {

    public ShopMenu(Plugin plugin) {
        super("Shop", 3, plugin);  // 3 rows
        addItem(new ItemBuilder(Material.DIAMOND).name("&bBuy").build(), 13);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onMenuClick(MenuClickEvent e) {
        if (!e.getInventory().equals(getInventory())) return;

        Player player = (Player) e.getWhoClicked();
        if (e.getSlot() == 13) {
            player.sendMessage("Purchased!");
        }
    }
}
```

### Constructors

```java
super("Name", plugin);                    // Hopper (5 slots)
super("Name", rows, plugin);              // Chest (1-6 rows)
super("Name", rows, fillPattern, plugin); // With fill item
```

### Methods

```java
menu.addItem(itemStack, slot);      // By index
menu.addItem(itemStack, x, y);      // By coordinates
menu.open(player);
menu.dispose();
menu.setDisposeOnClose(false);      // Keep menu after close
menu.setAllowIO(true);              // Allow shift-click
```

## PageableMenu

```java
PageableMenu menu = new PageableMenu("Items", 3, plugin);
menu.setNextButton(new ItemBuilder(Material.ARROW).name("&aNext").build());
menu.setPreviousButton(new ItemBuilder(Material.ARROW).name("&cBack").build());
menu.setItems(itemList);
menu.loadPage(0);
menu.open(player);
```

## ScrollableMenu

```java
ScrollableMenu menu = new ScrollableMenu("List", 6, plugin);
menu.setScrollUpButton(upButton);
menu.setScrollDownButton(downButton);
menu.setContent(itemList);
menu.open(player);
```

## ConfirmationMenu

```java
ConfirmationMenu confirm = new ConfirmationMenu(plugin, "Delete?", displayItem) {
    @Override
    protected Function<MenuClickEvent, Void> executeOnConfirmation() {
        return event -> { /* confirmed */ return null; };
    }

    @Override
    protected Function<MenuClickEvent, Void> executeOnCancellation() {
        return event -> { /* cancelled */ return null; };
    }
};
confirm.open(player);
```

## Cleanup

```java
@Override
public void onDisable() {
    menus.disposeAllMenus();
}
```
