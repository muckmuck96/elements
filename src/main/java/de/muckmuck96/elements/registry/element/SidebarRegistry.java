package de.muckmuck96.elements.registry.element;

import de.muckmuck96.elements.element.sidebar.SidebarHolder;
import de.muckmuck96.elements.element.sidebar.runner.SidebarRunner;
import de.muckmuck96.elements.registry.ElementRegistry;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SidebarRegistry extends ElementRegistry {
    private final Map<SidebarHolder, SidebarRunner> sidebarRunners = new ConcurrentHashMap<>();
    private final Plugin plugin;
    private PlaceholderRegistry placeholderRegistry;

    public SidebarRegistry(Plugin plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    /**
     * Sets the PlaceholderRegistry for all sidebars managed by this registry.
     *
     * @param placeholderRegistry the placeholder registry to use
     */
    public void setPlaceholderRegistry(PlaceholderRegistry placeholderRegistry) {
        this.placeholderRegistry = placeholderRegistry;
        for (SidebarRunner runner : sidebarRunners.values()) {
            runner.setPlaceholderRegistry(placeholderRegistry);
        }
    }

    /**
     * Registers and starts a new sidebar.
     *
     * @param sidebarHolder the sidebar configuration
     * @param isDefault     if true, sidebar is shown to all players automatically
     */
    public void register(SidebarHolder sidebarHolder, boolean isDefault) {
        if (sidebarRunners.containsKey(sidebarHolder)) {
            return;
        }
        SidebarRunner sidebarRunner = new SidebarRunner(plugin, sidebarHolder);
        if (placeholderRegistry != null) {
            sidebarRunner.setPlaceholderRegistry(placeholderRegistry);
        }
        sidebarRunner.runTaskTimerAsynchronously(plugin, 1L, 1L);
        sidebarRunner.setDefault(isDefault);
        sidebarRunners.put(sidebarHolder, sidebarRunner);
    }

    /**
     * Registers and starts a new default sidebar.
     *
     * @param sidebarHolder the sidebar configuration
     */
    public void register(SidebarHolder sidebarHolder) {
        register(sidebarHolder, true);
    }

    /**
     * Unregisters and stops a sidebar.
     *
     * @param sidebarHolder the sidebar to remove
     */
    public void unregister(SidebarHolder sidebarHolder) {
        SidebarRunner runner = sidebarRunners.remove(sidebarHolder);
        if (runner != null) {
            runner.destroy();
            runner.cancel();
        }
    }

    /**
     * Checks if a sidebar is registered.
     *
     * @param sidebarHolder the sidebar to check
     * @return true if registered
     */
    public boolean isRegistered(SidebarHolder sidebarHolder) {
        return sidebarRunners.containsKey(sidebarHolder);
    }

    /**
     * Clears all sidebars.
     */
    public void clearRunners() {
        for (SidebarRunner runner : sidebarRunners.values()) {
            runner.destroy();
            runner.cancel();
        }
        sidebarRunners.clear();
    }
}
