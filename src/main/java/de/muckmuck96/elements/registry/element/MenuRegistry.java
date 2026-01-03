package de.muckmuck96.elements.registry.element;

import de.muckmuck96.elements.element.menu.BaseMenu;
import de.muckmuck96.elements.element.menu.container.ConfirmationMenu;
import de.muckmuck96.elements.element.menu.container.PageableMenu;
import de.muckmuck96.elements.element.menu.container.ScrollableContainer;
import de.muckmuck96.elements.element.menu.container.ScrollableMenu;
import de.muckmuck96.elements.registry.ElementRegistry;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * Registry for managing menu instances with automatic lifecycle management.
 * <p>
 * Provides centralized tracking of all menus, automatic cleanup on plugin disable,
 * and convenient querying methods.
 * </p>
 *
 * <pre>
 * // Enable the registry
 * MenuRegistry menus = registry.enable(MenuRegistry.class);
 *
 * // Menus auto-register when using the plugin constructor
 * MyMenu menu = new MyMenu(plugin, menus);
 *
 * // Query menus
 * List&lt;BaseMenu&gt; playerMenus = menus.getMenusForPlayer(player);
 *
 * // Cleanup
 * menus.disposeAllMenus();
 * </pre>
 */
public class MenuRegistry extends ElementRegistry {
    private final List<BaseMenu> registeredMenus = new CopyOnWriteArrayList<>();
    private final Plugin plugin;

    public MenuRegistry(Plugin plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    /**
     * Registers a menu with this registry.
     *
     * @param menu the menu to register
     */
    public void registerMenu(BaseMenu menu) {
        if (menu != null && !registeredMenus.contains(menu)) {
            registeredMenus.add(menu);
        }
    }

    /**
     * Unregisters a menu from this registry.
     *
     * @param menu the menu to unregister
     */
    public void unregisterMenu(BaseMenu menu) {
        registeredMenus.remove(menu);
    }

    /**
     * Gets all registered menus.
     *
     * @return unmodifiable list of all registered menus
     */
    public List<BaseMenu> getRegisteredMenus() {
        return Collections.unmodifiableList(registeredMenus);
    }

    /**
     * Gets all menus currently being viewed by a specific player.
     *
     * @param player the player to check
     * @return list of menus the player is viewing
     */
    public List<BaseMenu> getMenusForPlayer(Player player) {
        return registeredMenus.stream()
                .filter(menu -> menu.getInventory() != null)
                .filter(menu -> menu.getInventory().getViewers().stream()
                        .anyMatch(viewer -> viewer.getUniqueId().equals(player.getUniqueId())))
                .collect(Collectors.toList());
    }

    /**
     * Gets all menus of a specific type.
     *
     * @param menuType the class of menu to filter by
     * @param <T>      the menu type
     * @return list of menus matching the type
     */
    @SuppressWarnings("unchecked")
    public <T extends BaseMenu> List<T> getMenusByType(Class<T> menuType) {
        return registeredMenus.stream()
                .filter(menuType::isInstance)
                .map(menu -> (T) menu)
                .collect(Collectors.toList());
    }

    /**
     * Gets all pageable menus.
     *
     * @return list of PageableMenu instances
     */
    public List<PageableMenu> getPageableMenus() {
        return getMenusByType(PageableMenu.class);
    }

    /**
     * Gets all scrollable menus.
     *
     * @return list of ScrollableMenu instances
     */
    public List<ScrollableMenu> getScrollableMenus() {
        return getMenusByType(ScrollableMenu.class);
    }

    /**
     * Gets all scrollable containers.
     *
     * @return list of ScrollableContainer instances
     */
    public List<ScrollableContainer> getScrollableContainers() {
        return getMenusByType(ScrollableContainer.class);
    }

    /**
     * Gets all confirmation menus.
     *
     * @return list of ConfirmationMenu instances
     */
    public List<ConfirmationMenu> getConfirmationMenus() {
        return getMenusByType(ConfirmationMenu.class);
    }

    /**
     * Finds a menu by its name.
     *
     * @param name the menu name
     * @return optional containing the menu if found
     */
    public Optional<BaseMenu> getMenuByName(String name) {
        return registeredMenus.stream()
                .filter(menu -> name.equals(menu.getName()))
                .findFirst();
    }

    /**
     * Closes all menus for a specific player.
     *
     * @param player the player whose menus should be closed
     */
    public void closeMenusForPlayer(Player player) {
        getMenusForPlayer(player).forEach(menu -> {
            player.closeInventory();
        });
    }

    /**
     * Disposes all menus for a specific player.
     *
     * @param player the player whose menus should be disposed
     */
    public void disposeMenusForPlayer(Player player) {
        List<BaseMenu> playerMenus = getMenusForPlayer(player);
        playerMenus.forEach(menu -> {
            player.closeInventory();
            menu.dispose();
            registeredMenus.remove(menu);
        });
    }

    /**
     * Disposes a specific menu and removes it from the registry.
     *
     * @param menu the menu to dispose
     */
    public void disposeMenu(BaseMenu menu) {
        if (menu != null) {
            menu.dispose();
            registeredMenus.remove(menu);
        }
    }

    /**
     * Disposes all registered menus.
     * Call this in your plugin's onDisable() method.
     */
    public void disposeAllMenus() {
        // Create a copy to avoid ConcurrentModificationException
        List<BaseMenu> menusCopy = List.copyOf(registeredMenus);
        for (BaseMenu menu : menusCopy) {
            try {
                menu.dispose();
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to dispose menu: " + e.getMessage());
            }
        }
        registeredMenus.clear();
    }

    /**
     * Gets the count of registered menus.
     *
     * @return number of registered menus
     */
    public int getMenuCount() {
        return registeredMenus.size();
    }

    /**
     * Checks if a menu is registered.
     *
     * @param menu the menu to check
     * @return true if the menu is registered
     */
    public boolean isRegistered(BaseMenu menu) {
        return registeredMenus.contains(menu);
    }
}
