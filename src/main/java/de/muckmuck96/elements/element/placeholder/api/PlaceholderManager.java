package de.muckmuck96.elements.element.placeholder.api;

import de.muckmuck96.elements.element.placeholder.classes.Bundle;
import de.muckmuck96.elements.element.placeholder.classes.Placeholder;
import de.muckmuck96.elements.element.placeholder.functions.PlaceholderFunction;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class PlaceholderManager {
    private final Map<Plugin, List<Placeholder>> placeholderList;
    private final List<Placeholder> globalPlaceholderList;

    public PlaceholderManager() {
        this.placeholderList = new ConcurrentHashMap<>();
        this.globalPlaceholderList = new CopyOnWriteArrayList<>();
    }


    public <T> void register(Plugin plugin, String key, PlaceholderFunction<T> pb) {
        Placeholder placeholder = new Placeholder.Builder().withKey(key).withPlaceholderFunction(pb).build();
        List<Placeholder> pluginPlaceholders = placeholderList.computeIfAbsent(plugin, k -> new CopyOnWriteArrayList<>());

        for (Placeholder iph : pluginPlaceholders) {
            if (placeholder.getKey().equals(iph.getKey())) {
                Bukkit.getLogger().warning("Placeholder " + key + " from " + plugin.getDescription().getName() + " is already registered");
                return;
            }
        }
        pluginPlaceholders.add(placeholder);
        String displayKey = placeholder.getKey().length() > 25 ? placeholder.getKey().substring(0, 25) + "..." : placeholder.getKey();
        Bukkit.getLogger().info("Private Placeholder " + displayKey + " registered");
    }

    public <T> void register(String key, PlaceholderFunction<T> pb) {
        Placeholder placeholder = new Placeholder.Builder().withKey(key).withPlaceholderFunction(pb).build();
        for (Placeholder iph : globalPlaceholderList) {
            if (placeholder.getKey().equals(iph.getKey())) {
                Bukkit.getLogger().warning("Placeholder " + key + " is already registered");
                return;
            }
        }
        globalPlaceholderList.add(placeholder);
        String displayKey = placeholder.getKey().length() > 25 ? placeholder.getKey().substring(0, 25) + "..." : placeholder.getKey();
        Bukkit.getLogger().info("Global Placeholder " + displayKey + " registered");
    }

    public <T> void register(Plugin plugin, List<String> keys, PlaceholderFunction<T> pb) {
        this.register(plugin, keys, pb, false);
    }

    public <T> void register(List<String> keys, PlaceholderFunction<T> pb) {
        this.register(keys, pb, false);
    }

    public <T> void register(Plugin plugin, List<String> keys, PlaceholderFunction<T> pb, boolean cleanup) {
        Placeholder placeholder = new Placeholder.Builder().withKeys(keys).withPlaceholderFunction(pb).withCleanup(cleanup).build();
        List<Placeholder> pluginPlaceholders = placeholderList.computeIfAbsent(plugin, k -> new CopyOnWriteArrayList<>());

        List<String> filteredKeys = new ArrayList<>(placeholder.getKeys());
        for (Placeholder iph : pluginPlaceholders) {
            filteredKeys.removeIf(key -> key.equals(iph.getKey()));
        }
        placeholder.setKeys(filteredKeys);
        pluginPlaceholders.add(placeholder);
        Bukkit.getLogger().info("Private Placeholder/s " + placeholder.getKeys() + " registered");
    }

    public <T> void register(List<String> keys, PlaceholderFunction<T> pb, boolean cleanup) {
        Placeholder placeholder = new Placeholder.Builder().withKeys(keys).withPlaceholderFunction(pb).withCleanup(cleanup).build();

        List<String> filteredKeys = new ArrayList<>(placeholder.getKeys());
        for (Placeholder iph : globalPlaceholderList) {
            filteredKeys.removeIf(key -> key.equals(iph.getKey()));
        }
        placeholder.setKeys(filteredKeys);
        globalPlaceholderList.add(placeholder);
        Bukkit.getLogger().info("Global Placeholder/s " + placeholder.getKeys() + " registered");
    }

    public String replace(Plugin plugin, String source, Bundle bundle) {
        List<Placeholder> pluginPlaceholders = placeholderList.get(plugin);
        if (pluginPlaceholders != null) {
            for (Placeholder iph : pluginPlaceholders) {
                source = replaceSingle(source, bundle, iph);
            }
        }
        return source;
    }

    public List<String> replace(Plugin plugin, List<String> source, Bundle bundle) {
        List<Placeholder> pluginPlaceholders = placeholderList.get(plugin);
        if (pluginPlaceholders == null) {
            return source;
        }

        List<String> result = new ArrayList<>(source.size());
        for (String entry : source) {
            for (Placeholder iph : pluginPlaceholders) {
                entry = replaceSingle(entry, bundle, iph);
            }
            result.add(entry);
        }
        return result;
    }

    public String replace(String source, Bundle bundle) {
        for (Placeholder iph : globalPlaceholderList) {
            source = replaceSingle(source, bundle, iph);
        }
        return source;
    }

    public List<String> replace(List<String> source, Bundle bundle) {
        List<String> result = new ArrayList<>(source.size());
        for (String entry : source) {
            for (Placeholder iph : globalPlaceholderList) {
                entry = replaceSingle(entry, bundle, iph);
            }
            result.add(entry);
        }
        return result;
    }

    private String replaceSingle(String entry, Bundle bundle, Placeholder iph) {
        if (iph.getKeys().size() > 1) {
            entry = replaceMultipleKeys(bundle, entry, iph);
            if (iph.isCleanup()) {
                entry = cleanupMultipleKeys(entry, iph);
            }
        } else {
            String replacement = safeRunPlaceholder(iph, bundle);
            entry = entry.replace(iph.getKey(), replacement);
            // Note: cleanup for single keys is handled by replace() above
            // which already replaces all occurrences
        }
        return entry;
    }

    private String safeRunPlaceholder(Placeholder iph, Bundle bundle) {
        if (iph.getPf() == null) {
            return "";
        }
        Object result = iph.getPf().run(bundle);
        return result != null ? String.valueOf(result) : "";
    }

    private String replaceMultipleKeys(Bundle bundle, String entry, Placeholder iph) {
        if (iph.getKeys().size() > 1) {
            if (iph.getPf() == null) {
                return entry;
            }
            Object rawValues = iph.getPf().run(bundle);
            if (!(rawValues instanceof Map<?, ?> rawMap)) {
                Bukkit.getLogger().warning("PlaceholderFunction for multi-key placeholder did not return a Map");
                return entry;
            }

            for (int i = 0; i < iph.getKeys().size(); i++) {
                Object keyValue = rawMap.get(i);
                if (keyValue instanceof List<?> rawList) {
                    for (Object v : rawList) {
                        String replacement = v != null ? String.valueOf(v) : "";
                        entry = entry.replace(iph.getKeys().get(i), replacement);
                    }
                }
            }
        } else {
            String replacement = safeRunPlaceholder(iph, bundle);
            entry = entry.replace(iph.getKey(), replacement);
        }
        return entry;
    }

    private String cleanupMultipleKeys(String entry, Placeholder iph) {
        if (iph.getKeys().size() > 1) {
            for (String key : iph.getKeys()) {
                entry = entry.replace(key, "");
            }
        } else {
            entry = entry.replace(iph.getKey(), "");
        }
        return entry;
    }
}
