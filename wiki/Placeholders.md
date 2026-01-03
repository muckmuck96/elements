# Placeholders

Dynamic text replacement for strings and lists.

## Setup

```java
PlaceholderRegistry placeholders = registry.enable(PlaceholderRegistry.class);
```

## Registration

```java
// Plugin-scoped
placeholders.register("{player}", bundle -> {
    Player p = bundle.getItem(Player.class);
    return p != null ? p.getName() : "Unknown";
});

// Global (all plugins)
placeholders.registerGlobal("{server}", bundle -> "My Server");
```

## Replacement

```java
Bundle bundle = new Bundle();
bundle.put(Player.class, player);

String result = placeholders.replace("Hello {player}!", bundle);
String all = placeholders.replaceAll("{player} on {server}", bundle);
```

## Bundle

```java
Bundle bundle = new Bundle();
bundle.put(Player.class, player);
bundle.put(Integer.class, 100);

Player p = bundle.getItem(Player.class);
```
