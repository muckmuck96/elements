package de.muckmuck96.elements.element.button;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

/**
 * Utility for reading item metadata and persistent data.
 */
public class ItemReader {

    @Nullable
    public static String getButtonSignature(ItemStack itemStack, NamespacedKey signatureKey) {
        return getButtonSignature(itemStack.getItemMeta(), signatureKey);
    }

    @Nullable
    public static String getButtonSignature(ItemMeta itemMeta, NamespacedKey signatureKey) {
        if (itemMeta == null || signatureKey == null) {
            return null;
        }
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        if (container.has(signatureKey, PersistentDataType.STRING)) {
            return container.get(signatureKey, PersistentDataType.STRING);
        }
        return null;
    }

    @Nullable
    public static <T, A> A getPDC(Plugin plugin, ItemStack itemStack, String key, PersistentDataType<T, A> dataType) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return null;
        return meta.getPersistentDataContainer().get(new NamespacedKey(plugin, key), dataType);
    }

    public static <T, A> void addPDC(Plugin plugin, ItemStack itemStack, String key, A thing, PersistentDataType<T, A> dataType) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return;
        itemMeta.getPersistentDataContainer().set(new NamespacedKey(plugin, key), dataType, thing);
        itemStack.setItemMeta(itemMeta);
    }
}
