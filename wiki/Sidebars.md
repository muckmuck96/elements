# Sidebars

Per-player scoreboards with dynamic content.

## Setup

```java
SidebarRegistry sidebars = registry.enable(SidebarRegistry.class);
```

## Creating Sidebars

```java
SidebarHolder holder = new SidebarHolder(
    List.of(
        "&6&lMY SERVER",
        "",
        "&7Player: &f{player}",
        "&7Coins: &e{coins}"
    ),
    20  // Update interval (ticks)
);

sidebars.register(holder);  // Registers as default sidebar (shown to all players)
```

## Placeholder Support

To use placeholders in sidebars, set the PlaceholderRegistry on the SidebarRegistry:

```java
PlaceholderRegistry placeholders = registry.enable(PlaceholderRegistry.class);
SidebarRegistry sidebars = registry.enable(SidebarRegistry.class);

// Register placeholders
placeholders.register("{player}", bundle -> {
    Player player = bundle.getItem(Player.class);
    return player != null ? player.getName() : "Unknown";
});

// Set placeholder support for sidebars
sidebars.setPlaceholderRegistry(placeholders);

// Create and register sidebar
SidebarHolder holder = new SidebarHolder(...);
sidebars.register(holder);
```

## Managing Sidebars

```java
// Register a sidebar (default = shown to all players)
sidebars.register(holder);

// Register non-default sidebar
sidebars.register(holder, false);

// Check if registered
boolean active = sidebars.isRegistered(holder);

// Unregister a specific sidebar
sidebars.unregister(holder);
```

## Cleanup

```java
@Override
public void onDisable() {
    sidebars.clearRunners();
}
```
