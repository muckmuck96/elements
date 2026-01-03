# Utilities

Helper classes for common operations.

## ItemBuilder

```java
ItemStack item = new ItemBuilder(Material.DIAMOND_SWORD)
    .name("&6Sword")
    .lore("&7Line 1")
    .enchant(Enchantment.DAMAGE_ALL, 5)
    .build();
```

## Skulls

```java
ItemStack head = new ItemBuilder(Material.PLAYER_HEAD)
    .skullOwner(player.getUniqueId())
    .build();
```

## Persistent Data

```java
ItemStack item = new ItemBuilder(Material.STICK)
    .addPDC(plugin, "key", 123, PersistentDataType.INTEGER)
    .build();

Integer value = ItemReader.getPDC(plugin, item, "key", PersistentDataType.INTEGER);
```
