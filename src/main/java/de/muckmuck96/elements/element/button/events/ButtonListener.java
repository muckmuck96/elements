package de.muckmuck96.elements.element.button.events;

import de.muckmuck96.elements.element.button.Button;
import de.muckmuck96.elements.element.button.ItemReader;
import de.muckmuck96.elements.registry.element.ButtonRegistry;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.Optional;

public class ButtonListener implements Listener {
    private final ButtonRegistry registry;

    public ButtonListener(ButtonRegistry registry) {
        this.registry = registry;
    }

    @EventHandler
    public void onButtonClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getCurrentItem() == null) return;

        ItemStack clicked = event.getCurrentItem();
        String clickedSignature = ItemReader.getButtonSignature(clicked, registry.getButtonSignature());
        if (clickedSignature == null) return;

        Optional<Button> clickedButton = registry.getRegisteredButtons().stream()
                .filter(registered -> matchesButton(registered, clicked, clickedSignature))
                .findFirst();

        clickedButton.ifPresent(button ->
                button.getEvent().onClick(event.getClickedInventory(), player, clicked, event.getClick()));
    }

    private boolean matchesButton(Button registered, ItemStack clicked, String clickedSignature) {
        ItemStack registeredItem = registered.getItemStack();
        if (registeredItem == null) return false;

        // First try to match by signature (unique button ID)
        String registeredSignature = ItemReader.getButtonSignature(registeredItem, registry.getButtonSignature());
        if (registeredSignature != null && Objects.equals(registeredSignature, clickedSignature)) {
            return true;
        }

        // Fall back to isSimilar which ignores stack size
        return registeredItem.isSimilar(clicked);
    }
}
