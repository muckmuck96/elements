package de.muckmuck96.elements.element.metadata.deserializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.Map;

public class ItemStackDeserializer extends JsonDeserializer<ItemStack> {
    @Override
    public ItemStack deserialize(JsonParser p, DeserializationContext deserializationContext) throws IOException, JacksonException {
        @SuppressWarnings("unchecked")
        Map<String, Object> data = p.readValueAs(Map.class);
        return ItemStack.deserialize(data);
    }
}
