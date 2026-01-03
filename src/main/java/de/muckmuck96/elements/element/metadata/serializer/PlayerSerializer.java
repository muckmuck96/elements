package de.muckmuck96.elements.element.metadata.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.bukkit.entity.Player;

import java.io.IOException;

public class PlayerSerializer extends JsonSerializer<Player> {
    @Override
    public void serialize(Player player, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("uuid", player.getUniqueId().toString());
        gen.writeEndObject();
    }
}
