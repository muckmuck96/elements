package de.muckmuck96.elements.element.button;

import de.muckmuck96.elements.Elements;
import de.muckmuck96.elements.element.button.events.ButtonClickEvent;
import de.muckmuck96.elements.registry.element.ButtonRegistry;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

/**
 * Represents a clickable button with an associated event handler.
 * <p>
 * Buttons can be auto-registered with the ButtonRegistry by using the
 * constructor that accepts a Plugin parameter.
 * </p>
 */
public class Button {
    private ItemStack itemStack;
    private ButtonClickEvent event;
    private Plugin plugin;
    private boolean autoRegistered = false;

    /**
     * Creates a button without auto-registration.
     * You must manually register this button with ButtonRegistry.
     *
     * @param itemStack the visual representation
     * @param event     the click handler
     */
    public Button(ItemStack itemStack, ButtonClickEvent event) {
        this.itemStack = itemStack;
        this.event = event;
    }

    /**
     * Creates a button with auto-registration.
     * If ButtonRegistry is enabled for the plugin, the button will be
     * automatically registered.
     *
     * @param plugin    the plugin that owns this button
     * @param itemStack the visual representation
     * @param event     the click handler
     */
    public Button(Plugin plugin, ItemStack itemStack, ButtonClickEvent event) {
        this.plugin = plugin;
        this.itemStack = itemStack;
        this.event = event;
        autoRegister();
    }

    private void autoRegister() {
        if (plugin == null) return;

        Elements.getRegistry(plugin).ifPresent(registry -> {
            registry.getElementRegistry(ButtonRegistry.class).ifPresent(buttonRegistry -> {
                buttonRegistry.registerButton(this);
                autoRegistered = true;
            });
        });
    }

    /**
     * Unregisters this button from the ButtonRegistry if it was auto-registered.
     */
    public void unregister() {
        if (!autoRegistered || plugin == null) return;

        Elements.getRegistry(plugin).ifPresent(registry -> {
            registry.getElementRegistry(ButtonRegistry.class).ifPresent(buttonRegistry -> {
                buttonRegistry.unregisterButton(this);
                autoRegistered = false;
            });
        });
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public ButtonClickEvent getEvent() {
        return event;
    }

    public void setEvent(ButtonClickEvent event) {
        this.event = event;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public boolean isAutoRegistered() {
        return autoRegistered;
    }
}
