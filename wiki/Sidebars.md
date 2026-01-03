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

SidebarRunner runner = sidebars.newRunner(holder, true);  // true = default sidebar
```

## Animated Content

```java
SidebarHolder page1 = new SidebarHolder(List.of("&aOnline: 50"), 40);
SidebarHolder page2 = new SidebarHolder(List.of("&bTPS: 20.0"), 40);

SidebarHolder main = new SidebarHolder(
    List.of("&6Server Stats"),
    20,
    page1, page2  // Cycles between these
);
```

## Managing Players

```java
List<BoardHolder> holders = runner.getHolders();
runner.unregisterHolder(player);
```

## Cleanup

```java
sidebars.removeRunner(runner);
runner.cancel();

@Override
public void onDisable() {
    sidebars.clearRunners();
}
```
