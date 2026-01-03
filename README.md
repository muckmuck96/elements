<p align="center">
  <img src="wiki/elements_logo.png" alt="Elements Logo" width="400">
</p>

<p align="center">
  A utility library for Paper 1.21+ plugin developers.
</p>

---

## Features

- **Buttons** - Clickable inventory items with event handling
- **Menus** - Inventory GUIs with pagination, scrolling, and confirmations
- **Sidebars** - Per-player scoreboards with animated content
- **Data Persistence** - Annotation-driven JSON/YAML storage
- **Placeholders** - Dynamic text replacement
- **Countdowns** - Scheduled tasks with lifecycle management

## Installation

Currently hosted via JitPack.

### Maven

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependency>
    <groupId>com.github.muckmuck96</groupId>
    <artifactId>elements</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Gradle

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.muckmuck96:elements:1.0.0'
}
```

## Quick Start

```java
public class MyPlugin extends JavaPlugin {
    private Registry registry;
    private MetadataRegistry metadata;

    @Override
    public void onEnable() {
        registry = Elements.register(this);

        // Enable registries you need
        registry.enable(ButtonRegistry.class);
        registry.enable(MenuRegistry.class);
        metadata = registry.enable(MetadataRegistry.class);
    }

    @Override
    public void onDisable() {
        if (metadata != null) metadata.saveAll();
    }
}
```

## Documentation

See the [Wiki](wiki/Home.md) for full documentation.

## Requirements

- Paper 1.21+
- Java 21+

## License

MIT
