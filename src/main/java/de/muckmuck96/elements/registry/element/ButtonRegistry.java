package de.muckmuck96.elements.registry.element;

import de.muckmuck96.elements.element.button.Button;
import de.muckmuck96.elements.element.button.events.ButtonListener;
import de.muckmuck96.elements.registry.ElementRegistry;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ButtonRegistry extends ElementRegistry {
    private final List<Button> registeredButtons = new ArrayList<>();
    private final NamespacedKey buttonSignature;

    public ButtonRegistry(Plugin plugin) {
        super(plugin);
        this.buttonSignature = new NamespacedKey(plugin, "elements_button_sign");
        Bukkit.getPluginManager().registerEvents(new ButtonListener(this), plugin);
    }

    public List<Button> getRegisteredButtons() {
        return Collections.unmodifiableList(registeredButtons);
    }

    public void registerButton(Button button) {
        registeredButtons.add(button);
    }

    public void unregisterButton(Button button) {
        registeredButtons.remove(button);
    }

    public NamespacedKey getButtonSignature() {
        return buttonSignature;
    }
}
