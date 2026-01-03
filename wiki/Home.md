# Elements Library

A utility library for Paper 1.21+ plugin developers.

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
    private MenuRegistry menus;
    private CountdownRegistry countdowns;
    private MetadataRegistry metadata;

    @Override
    public void onEnable() {
        registry = Elements.register(this);

        menus = registry.enable(MenuRegistry.class);
        countdowns = registry.enable(CountdownRegistry.class);
        metadata = registry.enable(MetadataRegistry.class);
    }

    @Override
    public void onDisable() {
        if (countdowns != null) countdowns.cancelAllCountdowns();
        if (menus != null) menus.disposeAllMenus();
        if (metadata != null) metadata.saveAll();
    }
}
```

## Documentation

- [Registry System](Registry)
- [Buttons](Buttons)
- [Menus](Menus)
- [Sidebars](Sidebars)
- [Countdowns](Countdowns)
- [Data Persistence](Data-Persistence)
- [Placeholders](Placeholders)
- [Utilities](Utilities)
