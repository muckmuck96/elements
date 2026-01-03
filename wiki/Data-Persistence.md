# Data Persistence

Annotation-driven JSON/YAML storage.

## Setup

```java
MetadataRegistry metadata = registry.enable(MetadataRegistry.class);

@Metadata(type = MetadataType.JSON)
public class ServerConfig {
    public String serverName = "My Server";
    public int maxPlayers = 100;
}

ServerConfig config = metadata.get(ServerConfig.class);
config.maxPlayers = 150;
metadata.saveAll();  // Call in onDisable()
```

## Per-Player Storage

```java
@Metadata(type = MetadataType.JSON, path = "players", name = "player_{id}")
public class PlayerData {
    public int coins = 0;
}

PlayerData data = metadata.get(PlayerData.class, player.getUniqueId());
metadata.save(PlayerData.class, player.getUniqueId(), data);
```

## YAML Config (auto-reload)

```java
@Metadata(type = MetadataType.YAML, config = true)
public class PluginConfig {
    public String welcomeMessage = "&aWelcome!";
}
```
