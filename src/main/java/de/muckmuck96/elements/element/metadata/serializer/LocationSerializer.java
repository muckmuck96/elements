package de.muckmuck96.elements.element.metadata.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.bukkit.Location;

import java.io.IOException;
import java.util.Map;

public class LocationSerializer extends JsonSerializer<Location> {
    @Override
    public void serialize(Location location, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        Map<String, Object> data = location.serialize();
        gen.writeObject(data);
    }
}
