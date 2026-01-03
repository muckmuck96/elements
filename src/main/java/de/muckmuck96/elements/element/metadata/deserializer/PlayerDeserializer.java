package de.muckmuck96.elements.element.metadata.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.UUID;

public class PlayerDeserializer extends JsonDeserializer<Player> {
    @Override
    public Player deserialize(JsonParser p, DeserializationContext deserializationContext) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        JsonNode uuidNode = node.path("uuid");
        if (uuidNode.isMissingNode() || uuidNode.isNull()) {
            return null;
        }
        String uuid = uuidNode.asText();
        if (uuid.isEmpty()) {
            return null;
        }
        try {
            return Bukkit.getPlayer(UUID.fromString(uuid));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
