# Buttons

Clickable inventory items with event handling.

## Setup

```java
ButtonRegistry buttons = registry.enable(ButtonRegistry.class);
```

## Creating Buttons

```java
Button button = new Button(
    plugin,
    new ItemBuilder(Material.DIAMOND).name("&bClick me!").build(),
    (inventory, player, item, clickType) -> {
        player.sendMessage("Clicked!");
    }
);
// Auto-registered when ButtonRegistry is enabled

// Unregister when done
button.unregister();
```

## ItemBuilder

```java
ItemStack item = new ItemBuilder(Material.DIAMOND_SWORD)
    .name("&6Legendary Sword")
    .lore("&7A powerful weapon")
    .enchant(Enchantment.DAMAGE_ALL, 5)
    .build();
```

## Button Events

```java
// Standard click event
Button button = new Button(plugin, item, (inv, player, item, click) -> {
    // handle click
});

// Position-aware button
Button button = new Button(item, new ExtendedButtonClickEvent(3, 2) {
    @Override
    public void onClick(Inventory inv, Player p, ItemStack i, ClickType c) {
        // getX(), getY() available
    }
});

// Decorative (no action)
Button decorative = new Button(item, ButtonDoNothingEvent.INSTANCE);
```
