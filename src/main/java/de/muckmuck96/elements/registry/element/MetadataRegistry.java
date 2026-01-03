package de.muckmuck96.elements.registry.element;

import de.muckmuck96.elements.element.metadata.DataHandler;
import de.muckmuck96.elements.registry.ElementRegistry;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Registry for managing metadata persistence via DataHandler.
 * <p>
 * Automatically initializes a DataHandler for the plugin's data folder
 * and provides convenient access to data persistence operations.
 * </p>
 *
 * <pre>
 * // Enable the registry
 * MetadataRegistry metadata = registry.enable(MetadataRegistry.class);
 *
 * // Load a metadata class
 * MyConfig config = metadata.get(MyConfig.class);
 *
 * // Save all metadata
 * metadata.saveAll();
 * </pre>
 */
public class MetadataRegistry extends ElementRegistry {
    private static final int DEFAULT_CACHE_SIZE = 1000;
    private static final int DEFAULT_PATTERN_CACHE_SIZE = 500;

    private final DataHandler dataHandler;
    private final Plugin plugin;

    public MetadataRegistry(Plugin plugin) {
        super(plugin);
        this.plugin = plugin;
        this.dataHandler = DataHandler.forPlugin((JavaPlugin) plugin);
    }

    /**
     * Creates a MetadataRegistry with custom cache sizes.
     *
     * @param plugin              the plugin instance
     * @param maxCacheSize        maximum number of cached singleton instances
     * @param maxPatternCacheSize maximum number of cached instances per pattern type
     */
    public MetadataRegistry(Plugin plugin, int maxCacheSize, int maxPatternCacheSize) {
        super(plugin);
        this.plugin = plugin;
        this.dataHandler = DataHandler.forPlugin((JavaPlugin) plugin, maxCacheSize, maxPatternCacheSize);
    }

    /**
     * Gets the underlying DataHandler instance.
     *
     * @return the DataHandler for this plugin
     */
    public DataHandler getDataHandler() {
        return dataHandler;
    }

    /**
     * Loads and returns a singleton metadata instance.
     *
     * @param clazz the class annotated with @Metadata
     * @param <T>   the type of the metadata class
     * @return the loaded or cached instance
     */
    public <T> T get(Class<T> clazz) {
        return dataHandler.get(clazz);
    }

    /**
     * Loads and returns a pattern-based metadata instance by string ID.
     *
     * @param clazz the class annotated with @Metadata (with {id} pattern)
     * @param id    the unique identifier
     * @param <T>   the type of the metadata class
     * @return the loaded or cached instance
     */
    public <T> T get(Class<T> clazz, String id) {
        return dataHandler.get(clazz, id);
    }

    /**
     * Loads and returns a pattern-based metadata instance by UUID.
     *
     * @param clazz the class annotated with @Metadata (with {id} pattern)
     * @param uuid  the unique identifier
     * @param <T>   the type of the metadata class
     * @return the loaded or cached instance
     */
    public <T> T get(Class<T> clazz, java.util.UUID uuid) {
        return dataHandler.get(clazz, uuid);
    }

    /**
     * Updates a singleton metadata instance in cache.
     *
     * @param clazz    the class annotated with @Metadata
     * @param instance the instance to cache
     * @param <T>      the type of the metadata class
     */
    public <T> void set(Class<T> clazz, T instance) {
        dataHandler.set(clazz, instance);
    }

    /**
     * Updates a pattern-based metadata instance in cache.
     *
     * @param clazz    the class annotated with @Metadata (with {id} pattern)
     * @param id       the unique identifier
     * @param instance the instance to cache
     * @param <T>      the type of the metadata class
     */
    public <T> void set(Class<T> clazz, String id, T instance) {
        dataHandler.set(clazz, id, instance);
    }

    /**
     * Updates a pattern-based metadata instance in cache.
     *
     * @param clazz    the class annotated with @Metadata (with {id} pattern)
     * @param uuid     the unique identifier
     * @param instance the instance to cache
     * @param <T>      the type of the metadata class
     */
    public <T> void set(Class<T> clazz, java.util.UUID uuid, T instance) {
        dataHandler.set(clazz, uuid, instance);
    }

    /**
     * Immediately saves a pattern-based metadata instance to disk.
     *
     * @param clazz    the class annotated with @Metadata (with {id} pattern)
     * @param id       the unique identifier
     * @param instance the instance to save
     * @param <T>      the type of the metadata class
     */
    public <T> void save(Class<T> clazz, String id, T instance) {
        dataHandler.save(clazz, id, instance);
    }

    /**
     * Immediately saves a pattern-based metadata instance to disk.
     *
     * @param clazz    the class annotated with @Metadata (with {id} pattern)
     * @param uuid     the unique identifier
     * @param instance the instance to save
     * @param <T>      the type of the metadata class
     */
    public <T> void save(Class<T> clazz, java.util.UUID uuid, T instance) {
        dataHandler.save(clazz, uuid, instance);
    }

    /**
     * Batch loads multiple metadata classes.
     *
     * @param classes the classes to load
     */
    @SafeVarargs
    public final void loadAll(Class<?>... classes) {
        for (Class<?> clazz : classes) {
            dataHandler.get(clazz);
        }
    }

    /**
     * Saves all cached editable metadata instances to disk.
     * Call this in your plugin's onDisable() method.
     */
    public void saveAll() {
        dataHandler.saveMetadatas();
    }

    /**
     * Clears the cache for a specific metadata class.
     *
     * @param clazz the class to clear from cache
     */
    public void clearCache(Class<?> clazz) {
        dataHandler.clearCache(clazz);
    }

    /**
     * Clears the cache for a specific pattern-based metadata instance.
     *
     * @param clazz the class to clear from cache
     * @param id    the specific ID to clear
     */
    public void clearCache(Class<?> clazz, String id) {
        dataHandler.clearCache(clazz, id);
    }

    /**
     * Clears the cache for a specific pattern-based metadata instance.
     *
     * @param clazz the class to clear from cache
     * @param uuid  the specific UUID to clear
     */
    public void clearCache(Class<?> clazz, java.util.UUID uuid) {
        dataHandler.clearCache(clazz, uuid);
    }

    /**
     * Manually registers a metadata class for use.
     *
     * @param clazz the class annotated with @Metadata
     */
    public void registerClass(Class<?> clazz) {
        dataHandler.registerClass(clazz);
    }
}
