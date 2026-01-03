package de.muckmuck96.elements.element.button;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ColorableArmorMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Fluent builder for creating customized ItemStacks.
 */
public class ItemBuilder {

    private final ItemStack itemStack;
    private ItemMeta itemMeta;
    private List<Component> lore;

    public ItemBuilder(Material material) {
        this.itemStack = new ItemStack(material);
        this.itemMeta = this.itemStack.getItemMeta();
        this.lore = new ArrayList<>();
    }

    public ItemBuilder(Material material, int amount) {
        this.itemStack = new ItemStack(material, amount);
        this.itemMeta = this.itemStack.getItemMeta();
        this.lore = new ArrayList<>();
    }

    public ItemBuilder(ItemStack itemStack) {
        this.itemStack = Objects.requireNonNull(itemStack, "itemStack cannot be null");
        this.itemMeta = itemStack.getItemMeta();
        if (itemMeta != null && itemMeta.hasLore()) {
            this.lore = new ArrayList<>(itemMeta.lore());
        } else {
            this.lore = new ArrayList<>();
        }
    }

    private void ensureMeta() {
        if (itemMeta == null) {
            throw new IllegalStateException("Cannot modify item without metadata (Material may be AIR)");
        }
    }

    public ItemBuilder name(String name) {
        ensureMeta();
        itemMeta.displayName(LegacyComponentSerializer.legacySection().deserialize(name).decoration(TextDecoration.ITALIC, false));
        return this;
    }

    public ItemBuilder name(Component name) {
        ensureMeta();
        itemMeta.displayName(name);
        return this;
    }

    public ItemBuilder color(Color color) {
        if (itemMeta instanceof ColorableArmorMeta colorableArmorMeta) {
            colorableArmorMeta.setColor(color);
        }
        return this;
    }

    public ItemBuilder enchant(Enchantment enchantment, int strength) {
        ensureMeta();
        itemMeta.addEnchant(enchantment, strength, true);
        return this;
    }

    public ItemBuilder addFlags(ItemFlag... flags) {
        itemStack.addItemFlags(flags);
        return this;
    }

    public ItemBuilder removeFlags(ItemFlag... flags) {
        itemStack.removeItemFlags(flags);
        return this;
    }

    public ItemBuilder shiny() {
        addFlags(ItemFlag.HIDE_ENCHANTS);
        return enchant(Enchantment.UNBREAKING, 1);
    }

    public ItemBuilder lore(String lore) {
        this.lore(Component.text(lore));
        return this;
    }

    public ItemBuilder lore(Component lore) {
        this.lore.add(lore);
        return this;
    }

    public ItemBuilder lore(String... lore) {
        for (String s : lore) {
            lore(s);
        }
        return this;
    }

    public ItemBuilder lore(List<String> lore) {
        this.lore = lore.stream().map(Component::text).collect(Collectors.toList());
        return this;
    }

    public ItemBuilder clearLore() {
        this.lore.clear();
        return this;
    }

    public ItemBuilder skullTexture(String texture) {
        if (itemMeta instanceof SkullMeta skullMeta) {
            PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
            profile.setProperty(new ProfileProperty("textures", texture));
            skullMeta.setPlayerProfile(profile);
            itemMeta = skullMeta;
        }
        return this;
    }

    public ItemBuilder skullOwner(String owner) {
        if (itemMeta instanceof SkullMeta skullMeta) {
            skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(owner));
            itemMeta = skullMeta;
        }
        return this;
    }

    public ItemBuilder skullOwner(UUID owner) {
        if (itemMeta instanceof SkullMeta skullMeta) {
            skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(owner));
            itemMeta = skullMeta;
        }
        return this;
    }

    public <T, A> ItemBuilder addPDC(Plugin plugin, String key, A thing, PersistentDataType<T, A> dataType) {
        ensureMeta();
        itemMeta.getPersistentDataContainer().set(new NamespacedKey(plugin, key), dataType, thing);
        return this;
    }

    public boolean hasMeta() {
        return itemMeta != null;
    }

    public ItemStack build() {
        if (itemMeta != null) {
            itemMeta.lore(lore);
            itemStack.setItemMeta(itemMeta);
        }
        return itemStack;
    }
}
