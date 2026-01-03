package de.muckmuck96.elements;

import de.muckmuck96.elements.registry.ElementRegistry;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

/**
 * Plugin-scoped container for element registries.
 */
public class Registry {
    private final Plugin plugin;
    private final Map<Class<? extends ElementRegistry>, ElementRegistry> enabledRegistries = new ConcurrentHashMap<>();

    private Registry(Plugin plugin) {
        this.plugin = plugin;
    }

    public static Registry init(Plugin plugin) {
        Registry registry = new Registry(plugin);
        Elements.addRegistry(registry);
        return registry;
    }

    public <T extends ElementRegistry> T enable(Class<T> registryClass) {
        try {
            T instance = registryClass.getDeclaredConstructor(Plugin.class).newInstance(plugin);
            enabledRegistries.put(registryClass, instance);
            return instance;
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to enable registry: " + registryClass.getName(), e);
            throw new RuntimeException("Failed to enable registry: " + registryClass.getName(), e);
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends ElementRegistry> Optional<T> getElementRegistry(Class<T> registryClass) {
        return Optional.ofNullable((T) enabledRegistries.get(registryClass));
    }

    @SuppressWarnings("unchecked")
    public <T extends ElementRegistry> T getElementRegistryOrThrow(Class<T> registryClass) {
        T registry = (T) enabledRegistries.get(registryClass);
        if (registry == null) {
            throw new IllegalStateException("Registry not enabled: " + registryClass.getName());
        }
        return registry;
    }

    public boolean isEnabled(Class<? extends ElementRegistry> registryClass) {
        return enabledRegistries.containsKey(registryClass);
    }

    public void disable(Class<? extends ElementRegistry> registryClass) {
        enabledRegistries.remove(registryClass);
    }

    public Plugin getPlugin() {
        return plugin;
    }
}
