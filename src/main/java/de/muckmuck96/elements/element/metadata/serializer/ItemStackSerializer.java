package de.muckmuck96.elements.element.metadata.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.Map;

public class ItemStackSerializer extends JsonSerializer<ItemStack> {
    @Override
    public void serialize(ItemStack itemStack, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        Map<String, Object> data = itemStack.serialize();
        gen.writeObject(data);
    }
}
