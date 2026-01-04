# Data Persistence

Annotation-driven JSON/YAML storage.

## Setup

```java
MetadataRegistry metadata = registry.enable(MetadataRegistry.class);

@Metadata(type = MetadataType.JSON)
public class ServerConfig {
    private String serverName = "My Server";
    private int maxPlayers = 100;

    public String getServerName() { return serverName; }
    public void setServerName(String serverName) { this.serverName = serverName; }

    public int getMaxPlayers() { return maxPlayers; }
    public void setMaxPlayers(int maxPlayers) { this.maxPlayers = maxPlayers; }
}

ServerConfig config = metadata.get(ServerConfig.class);
config.setMaxPlayers(150);
metadata.saveAll();  // Call in onDisable()
```

## Per-Player Storage

```java
@Metadata(type = MetadataType.JSON, path = "players", name = "player_{id}")
public class PlayerData {
    private int coins = 0;

    public int getCoins() { return coins; }
    public void setCoins(int coins) { this.coins = coins; }
}

PlayerData data = metadata.get(PlayerData.class, player.getUniqueId());
data.setCoins(data.getCoins() + 100);
metadata.save(PlayerData.class, player.getUniqueId(), data);
```

## YAML Config (auto-reload)

```java
@Metadata(type = MetadataType.YAML, config = true)
public class PluginConfig {
    private String welcomeMessage = "&aWelcome!";

    public String getWelcomeMessage() { return welcomeMessage; }
    public void setWelcomeMessage(String welcomeMessage) { this.welcomeMessage = welcomeMessage; }
}
```
