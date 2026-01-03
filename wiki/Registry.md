# Registry System

Plugin-scoped registry management.

## Initialization

```java
public class MyPlugin extends JavaPlugin {
    private Registry registry;

    @Override
    public void onEnable() {
        registry = Elements.register(this);
    }
}
```

## Enabling Registries

```java
ButtonRegistry buttons = registry.enable(ButtonRegistry.class);
MenuRegistry menus = registry.enable(MenuRegistry.class);
SidebarRegistry sidebars = registry.enable(SidebarRegistry.class);
CountdownRegistry countdowns = registry.enable(CountdownRegistry.class);
PlaceholderRegistry placeholders = registry.enable(PlaceholderRegistry.class);
MetadataRegistry metadata = registry.enable(MetadataRegistry.class);
```

## Accessing Registries

```java
Optional<ButtonRegistry> buttons = registry.getElementRegistry(ButtonRegistry.class);
```

## Custom Registries

```java
public class MyRegistry extends ElementRegistry {
    public MyRegistry(Plugin plugin) {
        super(plugin);
    }
}

MyRegistry myRegistry = registry.enable(MyRegistry.class);
```
