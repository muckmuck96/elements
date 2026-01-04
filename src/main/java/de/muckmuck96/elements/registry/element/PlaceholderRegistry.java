package de.muckmuck96.elements.registry.element;

import de.muckmuck96.elements.element.placeholder.api.PlaceholderManager;
import de.muckmuck96.elements.element.placeholder.classes.Bundle;
import de.muckmuck96.elements.element.placeholder.functions.PlaceholderFunction;
import de.muckmuck96.elements.registry.ElementRegistry;
import org.bukkit.plugin.Plugin;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Registry for managing Placeholder instances with automatic lifecycle management.
 * <p>
 * Provides centralized tracking of all placeholders registered by a plugin and
 * convenient methods for registration and replacement operations.
 * </p>
 *
 * <pre>
 * // Enable the registry
 * PlaceholderRegistry placeholders = registry.enable(PlaceholderRegistry.class);
 *
 * // Register placeholders
 * placeholders.register("player_name", bundle -&gt; bundle.getPlayer().getName());
 * placeholders.registerGlobal("server_name", bundle -&gt; "My Server");
 *
 * // Replace placeholders in text
 * String result = placeholders.replace("Hello {player_name}!", bundle);
 * </pre>
 */
public class PlaceholderRegistry extends ElementRegistry {
    private final PlaceholderManager manager;
    private final Plugin plugin;
    private final List<String> registeredKeys = new CopyOnWriteArrayList<>();
    private final List<String> registeredGlobalKeys = new CopyOnWriteArrayList<>();

    public PlaceholderRegistry(Plugin plugin) {
        super(plugin);
        this.plugin = plugin;
        this.manager = new PlaceholderManager();
    }

    /**
     * Registers a plugin-scoped placeholder.
     *
     * @param key the placeholder key (e.g., "{player_name}")
     * @param function the function to generate the replacement value
     */
    public <T> void register(String key, PlaceholderFunction<T> function) {
        manager.register(plugin, key, function);
        registeredKeys.add(key);
    }

    /**
     * Registers multiple plugin-scoped placeholder keys with a single function.
     *
     * @param keys the placeholder keys
     * @param function the function to generate the replacement values
     */
    public <T> void register(List<String> keys, PlaceholderFunction<T> function) {
        manager.register(plugin, keys, function);
        registeredKeys.addAll(keys);
    }

    /**
     * Registers multiple plugin-scoped placeholder keys with cleanup support.
     *
     * @param keys the placeholder keys
     * @param function the function to generate the replacement values
     * @param cleanup whether to clean up unreplaced keys
     */
    public <T> void register(List<String> keys, PlaceholderFunction<T> function, boolean cleanup) {
        manager.register(plugin, keys, function, cleanup);
        registeredKeys.addAll(keys);
    }

    /**
     * Registers a global placeholder (available to all plugins).
     *
     * @param key the placeholder key
     * @param function the function to generate the replacement value
     */
    public <T> void registerGlobal(String key, PlaceholderFunction<T> function) {
        manager.register(key, function);
        registeredGlobalKeys.add(key);
    }

    /**
     * Registers multiple global placeholder keys with a single function.
     *
     * @param keys the placeholder keys
     * @param function the function to generate the replacement values
     */
    public <T> void registerGlobal(List<String> keys, PlaceholderFunction<T> function) {
        manager.register(keys, function);
        registeredGlobalKeys.addAll(keys);
    }

    /**
     * Registers multiple global placeholder keys with cleanup support.
     *
     * @param keys the placeholder keys
     * @param function the function to generate the replacement values
     * @param cleanup whether to clean up unreplaced keys
     */
    public <T> void registerGlobal(List<String> keys, PlaceholderFunction<T> function, boolean cleanup) {
        manager.register(keys, function, cleanup);
        registeredGlobalKeys.addAll(keys);
    }

    /**
     * Replaces plugin-scoped placeholders in the given text.
     *
     * @param source the source text containing placeholders
     * @param bundle the data bundle for placeholder resolution
     * @return the text with placeholders replaced
     */
    public String replace(String source, Bundle bundle) {
        return manager.replace(plugin, source, bundle);
    }

    /**
     * Replaces plugin-scoped placeholders in multiple strings.
     *
     * @param source the list of strings containing placeholders
     * @param bundle the data bundle for placeholder resolution
     * @return list of strings with placeholders replaced
     */
    public List<String> replace(List<String> source, Bundle bundle) {
        return manager.replace(plugin, source, bundle);
    }

    /**
     * Replaces global placeholders in the given text.
     *
     * @param source the source text containing placeholders
     * @param bundle the data bundle for placeholder resolution
     * @return the text with placeholders replaced
     */
    public String replaceGlobal(String source, Bundle bundle) {
        return manager.replace(source, bundle);
    }

    /**
     * Replaces global placeholders in multiple strings.
     *
     * @param source the list of strings containing placeholders
     * @param bundle the data bundle for placeholder resolution
     * @return list of strings with placeholders replaced
     */
    public List<String> replaceGlobal(List<String> source, Bundle bundle) {
        return manager.replace(source, bundle);
    }

    /**
     * Replaces both plugin-scoped and global placeholders.
     *
     * @param source the source text containing placeholders
     * @param bundle the data bundle for placeholder resolution
     * @return the text with all placeholders replaced
     */
    public String replaceAll(String source, Bundle bundle) {
        String result = manager.replace(plugin, source, bundle);
        return manager.replace(result, bundle);
    }

    /**
     * Replaces both plugin-scoped and global placeholders in multiple strings.
     *
     * @param source the list of strings containing placeholders
     * @param bundle the data bundle for placeholder resolution
     * @return list of strings with all placeholders replaced
     */
    public List<String> replaceAll(List<String> source, Bundle bundle) {
        List<String> result = manager.replace(plugin, source, bundle);
        return manager.replace(result, bundle);
    }

    /**
     * Gets all registered plugin-scoped placeholder keys.
     *
     * @return unmodifiable list of registered keys
     */
    public List<String> getRegisteredKeys() {
        return Collections.unmodifiableList(registeredKeys);
    }

    /**
     * Gets all registered global placeholder keys.
     *
     * @return unmodifiable list of registered global keys
     */
    public List<String> getRegisteredGlobalKeys() {
        return Collections.unmodifiableList(registeredGlobalKeys);
    }

    /**
     * Gets the underlying PlaceholderManager for advanced operations.
     *
     * @return the PlaceholderManager instance
     */
    public PlaceholderManager getManager() {
        return manager;
    }

    /**
     * Gets the count of registered plugin-scoped placeholders.
     *
     * @return count of registered placeholders
     */
    public int getPlaceholderCount() {
        return registeredKeys.size();
    }

    /**
     * Gets the count of registered global placeholders.
     *
     * @return count of registered global placeholders
     */
    public int getGlobalPlaceholderCount() {
        return registeredGlobalKeys.size();
    }
}
