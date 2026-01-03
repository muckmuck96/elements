package de.muckmuck96.elements;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class Elements extends JavaPlugin {

    private static final List<Registry> initializedRegistries = new ArrayList<>();

    @Override
    public void onEnable() {
        getLogger().info("Elements library loaded");
    }

    @Override
    public void onDisable() {
        initializedRegistries.clear();
        getLogger().info("Elements library unloaded");
    }

    public static List<Registry> getInitializedRegistries() {
        return Collections.unmodifiableList(initializedRegistries);
    }

    static void addRegistry(Registry registry) {
        initializedRegistries.add(registry);
    }

    static void removeRegistry(Registry registry) {
        initializedRegistries.remove(registry);
    }

    public static Registry register(Plugin plugin) {
        return Registry.init(plugin);
    }

    /**
     * Finds the Registry associated with the given plugin.
     *
     * @param plugin the plugin to find the registry for
     * @return an Optional containing the Registry if found
     */
    public static Optional<Registry> getRegistry(Plugin plugin) {
        return initializedRegistries.stream()
                .filter(r -> r.getPlugin().equals(plugin))
                .findFirst();
    }
}
