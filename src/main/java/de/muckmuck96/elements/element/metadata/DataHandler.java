package de.muckmuck96.elements.element.metadata;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import de.muckmuck96.elements.element.metadata.deserializer.ItemStackDeserializer;
import de.muckmuck96.elements.element.metadata.deserializer.LocationDeserializer;
import de.muckmuck96.elements.element.metadata.deserializer.PlayerDeserializer;
import de.muckmuck96.elements.element.metadata.serializer.ItemStackSerializer;
import de.muckmuck96.elements.element.metadata.serializer.LocationSerializer;
import de.muckmuck96.elements.element.metadata.serializer.PlayerSerializer;
import de.muckmuck96.elements.element.metadata.validator.MetadataValidationException;
import de.muckmuck96.elements.element.metadata.validator.MetadataValidator;
import de.muckmuck96.elements.element.metadata.validator.MetadataValueValidator;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JSON/YAML data persistence handler with caching and validation.
 */
public final class DataHandler {
    private static final Logger LOGGER = Logger.getLogger(DataHandler.class.getName());
    private static final ObjectMapper JSON_MAPPER;
    private static final ObjectMapper YAML_MAPPER;
    private static final int DEFAULT_MAX_CACHE_SIZE = 1000;
    private static final int DEFAULT_MAX_PATTERN_CACHE_SIZE = 500;

    static {
        SimpleModule playerModule = new SimpleModule("PlayerModule")
                .addSerializer(org.bukkit.entity.Player.class, new PlayerSerializer())
                .addDeserializer(org.bukkit.entity.Player.class, new PlayerDeserializer());

        SimpleModule itemStackModule = new SimpleModule("ItemStackModule")
                .addSerializer(ItemStack.class, new ItemStackSerializer())
                .addDeserializer(ItemStack.class, new ItemStackDeserializer());

        SimpleModule locationModule = new SimpleModule("LocationModule")
                .addSerializer(Location.class, new LocationSerializer())
                .addDeserializer(Location.class, new LocationDeserializer());

        JSON_MAPPER = JsonMapper.builder()
                .addModule(playerModule)
                .addModule(itemStackModule)
                .addModule(locationModule)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true)
                .visibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
                .visibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.PUBLIC_ONLY)
                .visibility(PropertyAccessor.SETTER, JsonAutoDetect.Visibility.PUBLIC_ONLY)
                .build();

        YAML_MAPPER = JsonMapper.builder(new YAMLFactory())
                .addModule(playerModule)
                .addModule(itemStackModule)
                .addModule(locationModule)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true)
                .visibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
                .visibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.PUBLIC_ONLY)
                .visibility(PropertyAccessor.SETTER, JsonAutoDetect.Visibility.PUBLIC_ONLY)
                .build();
    }

    private static final class Cached {
        final Object value;
        final long lastModified; // -1 if file didn't exist or couldn't read

        Cached(Object value, long lastModified) {
            this.value = value;
            this.lastModified = lastModified;
        }
    }

    private final Path baseDir;
    private final int maxCacheSize;
    private final int maxPatternCacheSize;
    private final Map<Class<?>, MetadataInfo> registry = new ConcurrentHashMap<>();
    private final Map<Class<?>, Cached> instances;
    private final Map<Class<?>, Map<String, Cached>> patternInstances = new ConcurrentHashMap<>();

    public DataHandler(Path baseDir) {
        this(baseDir, DEFAULT_MAX_CACHE_SIZE, DEFAULT_MAX_PATTERN_CACHE_SIZE);
    }

    public DataHandler(Path baseDir, int maxCacheSize, int maxPatternCacheSize) {
        this.baseDir = baseDir;
        this.maxCacheSize = maxCacheSize;
        this.maxPatternCacheSize = maxPatternCacheSize;
        this.instances = Collections.synchronizedMap(createLruCache(maxCacheSize));
        try {
            Files.createDirectories(baseDir);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to create data directory: " + baseDir, e);
        }
    }

    private static <K, V> LinkedHashMap<K, V> createLruCache(int maxSize) {
        return new LinkedHashMap<>(16, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > maxSize;
            }
        };
    }

    public static DataHandler forPlugin(JavaPlugin plugin) {
        return new DataHandler(plugin.getDataFolder().toPath());
    }

    public static DataHandler forPlugin(JavaPlugin plugin, int maxCacheSize, int maxPatternCacheSize) {
        return new DataHandler(plugin.getDataFolder().toPath(), maxCacheSize, maxPatternCacheSize);
    }

    public static ObjectMapper jsonMapper() { return JSON_MAPPER; }
    public static ObjectMapper yamlMapper() { return YAML_MAPPER; }

    public static <T> T load(String path, Class<T> type) {
        try {
            return JSON_MAPPER.readValue(new File(path), type);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to load JSON from path: " + path, e);
            return null;
        }
    }

    public static <T> T loadOrInit(String path, Class<T> type) {
        try {
            return JSON_MAPPER.readValue(new File(path), type);
        } catch (IOException e) {
            LOGGER.log(Level.FINE, "File not found or unreadable, creating new instance: " + path, e);
            try {
                return type.getDeclaredConstructor().newInstance();
            } catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException ex) {
                LOGGER.log(Level.SEVERE, "Failed to instantiate default for type: " + type.getName(), ex);
                return null;
            }
        }
    }

    public static <T> void save(String path, T object) {
        try {
            Path p = Path.of(path);
            Files.createDirectories(p.getParent());
            JSON_MAPPER.writer().with(JsonGenerator.Feature.AUTO_CLOSE_TARGET).writeValue(p.toFile(), object);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // ---------- Lazy annotation-driven storage (JSON + YAML) ----------

    @SafeVarargs
    public final <T> void loadMetadatas(Class<T>... classes) {
        for (Class<T> c : classes) { get(c); }
    }

    public void saveMetadatas() {
        for (Map.Entry<Class<?>, Cached> e : instances.entrySet()) {
            Class<?> clazz = e.getKey();
            Cached cached = e.getValue();
            MetadataInfo info = registry.get(clazz);
            // Skip: null info, pattern-based, not editable, or config (user-managed)
            if (info == null || info.pattern || !info.editable || info.config) continue;
            Path file = resolveFile(info, null);
            saveGeneric(info, cached.value, null, file);

            long lm = getLastModified(file);
            instances.put(clazz, new Cached(cached.value, lm));
        }

        // pattern-based storages
        for (Map.Entry<Class<?>, Map<String, Cached>> entry : patternInstances.entrySet()) {
            Class<?> clazz = entry.getKey();
            MetadataInfo info = registry.get(clazz);
            // Skip: null info, not pattern-based, not editable, or config (user-managed)
            if (info == null || !info.pattern || !info.editable || info.config) continue;

            Map<String, Cached> perId = entry.getValue();
            for (Map.Entry<String, Cached> idEntry : perId.entrySet()) {
                String id = idEntry.getKey();
                Cached cached = idEntry.getValue();

                Path file = resolveFile(info, id);
                saveGeneric(info, cached.value, id, file);

                long lm = getLastModified(file);
                perId.put(id, new Cached(cached.value, lm));
            }
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> clazz) {
        ensureRegistered(clazz);
        MetadataInfo info = registry.get(clazz);
        if (info == null) return null;
        if (info.pattern) {
            throw new IllegalStateException("Metadata " + clazz.getName()
                    + " is pattern-based (name contains {id}); use get(clazz, id) instead.");
        }

        Path file = resolveFile(info, null);
        Cached cached = instances.get(clazz);

        if (!info.config) {
            // normal storage: cache once, no auto reload
            if (cached != null) {
                return (T) cached.value;
            }
            Object value = loadGenericOrDefault(info, null, file);
            long lm = getLastModified(file);
            instances.put(clazz, new Cached(value, lm));
            return (T) value;
        }

        // config storage: check lastModified each time
        long currentLastModified = getLastModified(file);
        if (cached != null && cached.lastModified == currentLastModified) {
            return (T) cached.value;
        }

        Object value = loadGenericOrDefault(info, null, file);
        long lm = getLastModified(file);
        instances.put(clazz, new Cached(value, lm));
        return (T) value;
    }

    public <T> void set(Class<T> clazz, T instance) {
        ensureRegistered(clazz);
        MetadataInfo info = registry.get(clazz);
        if (info == null) {
            throw new IllegalStateException("Class " + clazz.getName() + " not annotated with @Metadata.");
        }
        if (!info.editable) {
            throw new IllegalStateException("Metadata " + clazz.getName() + " is read-only (editable=false).");
        }
        Path file = resolveFile(info, null);
        long lm = getLastModified(file);
        instances.put(clazz, new Cached(instance, lm));
    }

    public <T> void set(Class<T> clazz, String id, T instance) {
        ensureRegistered(clazz);
        MetadataInfo info = registry.get(clazz);
        if (info == null) {
            throw new IllegalStateException("Class " + clazz.getName() + " not annotated with @Metadata.");
        }
        if (!info.pattern) {
            throw new IllegalStateException("Metadata " + clazz.getName()
                    + " is not pattern-based; use set(clazz, instance) instead.");
        }
        if (!info.editable) {
            throw new IllegalStateException("Metadata " + clazz.getName() + " is read-only (editable=false).");
        }
        Path file = resolveFile(info, id);
        long lm = getLastModified(file);
        patternInstances
                .computeIfAbsent(clazz, k -> Collections.synchronizedMap(createLruCache(maxPatternCacheSize)))
                .put(id, new Cached(instance, lm));
    }

    public <T> void set(Class<T> clazz, UUID uuid, T instance) {
        set(clazz, uuid.toString(), instance);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> clazz, String id) {
        ensureRegistered(clazz);
        MetadataInfo info = registry.get(clazz);
        if (info == null) {
            throw new IllegalStateException("Class " + clazz.getName() + " is not annotated with @Metadata.");
        }
        if (!info.pattern) {
            throw new IllegalStateException("Metadata " + clazz.getName()
                    + " is not pattern-based; use get(clazz) instead of get(clazz, id).");
        }

        Path file = resolveFile(info, id);
        Map<String, Cached> perIdMap = patternInstances.computeIfAbsent(clazz,
                k -> Collections.synchronizedMap(createLruCache(maxPatternCacheSize)));
        Cached cached = perIdMap.get(id);

        if (!info.config) {
            // normal pattern storage: cache once
            if (cached != null) {
                return (T) cached.value;
            }
            Object value = loadGenericOrDefault(info, id, file);
            long lm = getLastModified(file);
            perIdMap.put(id, new Cached(value, lm));
            return (T) value;
        }

        // config-style pattern storage: auto reload when file changes
        long currentLastModified = getLastModified(file);
        if (cached != null && cached.lastModified == currentLastModified) {
            return (T) cached.value;
        }

        Object value = loadGenericOrDefault(info, id, file);
        long lm = getLastModified(file);
        perIdMap.put(id, new Cached(value, lm));
        return (T) value;
    }

    public <T> void save(Class<T> clazz, String id, T instance) {
        ensureRegistered(clazz);
        MetadataInfo info = registry.get(clazz);
        if (info == null) {
            throw new IllegalStateException("Class " + clazz.getName() + " is not annotated with @Metadata.");
        }
        if (!info.pattern) {
            throw new IllegalStateException("Metadata " + clazz.getName()
                    + " is not pattern-based; use saveMetadatas()/set(clazz, obj) instead.");
        }
        if (!info.editable) {
            throw new IllegalStateException("Metadata " + clazz.getName() + " is read-only (editable=false).");
        }

        Path file = resolveFile(info, id);
        saveGeneric(info, instance, id, file);

        long lm = getLastModified(file);

        patternInstances
                .computeIfAbsent(clazz, k -> Collections.synchronizedMap(createLruCache(maxPatternCacheSize)))
                .put(id, new Cached(instance, lm));
    }

    public <T> T get(Class<T> clazz, UUID uuid) {
        return get(clazz, uuid.toString());
    }

    public <T> void save(Class<T> clazz, UUID uuid, T instance) {
        save(clazz, uuid.toString(), instance);
    }

    public void registerClass(Class<?> c) {
        Metadata metadata = c.getAnnotation(Metadata.class);
        if (metadata == null) throw new IllegalArgumentException("Class " + c.getName() + " missing @Metadata");
        MetadataInfo info = toInfo(c, metadata);
        registry.put(c, info);
    }

    public void clearCache(Class<?> clazz) {
        instances.remove(clazz);
        patternInstances.remove(clazz);
    }

    public void clearCache(Class<?> clazz, String id) {
        Map<String, Cached> map = patternInstances.get(clazz);
        if (map != null) map.remove(id);
    }

    public void clearCache(Class<?> clazz, UUID uuid) {
        clearCache(clazz, uuid.toString());
    }

    // ---------- Internals ----------
    private void validateMetadataObject(Object obj) {
        if (obj == null) return;

        Class<?> clazz = obj.getClass();

        for (Field field : clazz.getDeclaredFields()) {
            MetadataValidator ann = field.getAnnotation(MetadataValidator.class);
            if (ann == null) continue;

            field.setAccessible(true);
            Object value;
            try {
                value = field.get(obj);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to access field '" + field.getName()
                        + "' on " + clazz.getName(), e);
            }

            MetadataValueValidator<?> validator;
            try {
                validator = ann.value().getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException("Could not instantiate validator "
                        + ann.value().getName() + " for field '" + field.getName()
                        + "' on " + clazz.getName(), e);
            }

            @SuppressWarnings("unchecked")
            MetadataValueValidator<Object> typed = (MetadataValueValidator<Object>) validator;

            try {
                typed.validate(value, field, clazz, ann);
            } catch (MetadataValidationException ex) {
                throw new RuntimeException(
                        "Validation failed for field '" + field.getName()
                                + "' of " + clazz.getSimpleName() + ": " + ex.getMessage(),
                        ex
                );
            }
        }
    }





    private long getLastModified(Path file) {
        try {
            if (Files.exists(file)) {
                return Files.getLastModifiedTime(file).toMillis();
            }
        } catch (IOException ignored) {}
        return -1L;
    }

    private void ensureRegistered(Class<?> c) {
        if (registry.containsKey(c)) return;
        Metadata metadata = c.getAnnotation(Metadata.class);
        if (metadata == null) return;
        MetadataInfo info = toInfo(c, metadata);
        registry.put(c, info);
    }

    private MetadataInfo toInfo(Class<?> c, Metadata metadata) {
        String subDir = metadata.path().isEmpty() ? "" : metadata.path();
        String nameTemplate = metadata.name().isEmpty() ? c.getSimpleName() : metadata.name();
        boolean pattern = nameTemplate.contains("{id}");

        return new MetadataInfo(c, metadata.type(), subDir, nameTemplate, pattern, metadata.editable(), metadata.autoMigrate(), metadata.config());
    }

    private ObjectMapper mapperFor(MetadataType type) {
        return type == MetadataType.YAML ? YAML_MAPPER : JSON_MAPPER;
    }

    private Object loadGenericOrDefault(MetadataInfo info, String idOrNull, Path filePath) {
        try {
            Files.createDirectories(filePath.getParent());
            ObjectMapper mapper = mapperFor(info.type);
            if(!Files.exists(filePath)) {
                // File doesn't exist - create with defaults
                Object def = newDefault(info.clazz);
                validateMetadataObject(def);
                if(info.editable) {
                    mapper.writerWithDefaultPrettyPrinter().writeValue(filePath.toFile(), def);
                }
                return def;
            }
            // File exists - read directly (Jackson uses field defaults for missing properties)
            Object loaded = mapper.readValue(filePath.toFile(), info.clazz);
            validateMetadataObject(loaded);
            if(info.autoMigrate && info.editable) {
                // Only re-save to add new fields that don't exist in the file yet
                mapper.writerWithDefaultPrettyPrinter().writeValue(filePath.toFile(), loaded);
            }
            return loaded;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load " + info.type + " for " + info.clazz.getName() + " (id=" + idOrNull + ")", e);
        }
    }

    private void saveGeneric(MetadataInfo info, Object obj, String idOrNull, Path filePath) {
        if(!info.editable) return;
        try {
            Files.createDirectories(filePath.getParent());
            mapperFor(info.type).writerWithDefaultPrettyPrinter().writeValue(filePath.toFile(), obj);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save " + info.type + " for " + info.clazz.getName() + " (id=" + idOrNull + ")", e);
        }
    }

    private Object newDefault(Class<?> cls) {
        try { return cls.getDeclaredConstructor().newInstance(); }
        catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new IllegalStateException("No-arg constructor required for @Metadata class: " + cls.getName(), e);
        }
    }

    private Path resolveFile(MetadataInfo info, String idOrNull) {
        String fileName;

        if (info.pattern) {
            if (idOrNull == null) {
                throw new IllegalArgumentException("Pattern storage " + info.clazz.getName() + " requires an id.");
            }
            validateId(idOrNull);
            fileName = info.nameTemplate.replace("{id}", idOrNull);
        } else {
            fileName = info.nameTemplate;
        }

        String ext = info.type == MetadataType.YAML ? ".yml" : ".json";

        Path resolved;
        if (info.path == null || info.path.isEmpty()) {
            resolved = baseDir.resolve(fileName + ext);
        } else {
            resolved = baseDir.resolve(info.path).resolve(fileName + ext);
        }

        // Ensure resolved path is within baseDir (prevent path traversal)
        if (!resolved.normalize().startsWith(baseDir.normalize())) {
            throw new SecurityException("Path traversal attempt detected: " + idOrNull);
        }

        return resolved;
    }

    private void validateId(String id) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("ID cannot be null or empty");
        }
        // Reject path traversal attempts and invalid characters
        if (id.contains("..") || id.contains("/") || id.contains("\\") ||
            id.contains("\0") || id.contains(":")) {
            throw new IllegalArgumentException("ID contains invalid characters: " + id);
        }
    }

}


