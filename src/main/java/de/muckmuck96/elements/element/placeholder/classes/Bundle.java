package de.muckmuck96.elements.element.placeholder.classes;

import de.muckmuck96.elements.element.placeholder.functions.MultiReplacementFunction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Container for passing typed data to placeholder functions.
 */
public class Bundle {
    private final Map<Class<?>, Object> items;
    private List<?> keys;
    private Map<Integer, List<String>> values;

    public Bundle() {
        this.items = new HashMap<>();
        this.keys = new ArrayList<>();
        this.values = new HashMap<>();
    }

    public <T> void put(Class<T> clazz, T obj) {
        this.items.put(clazz, obj);
    }

    public <T> T getItem(Class<T> clazz) {
        return clazz.cast(items.get(clazz));
    }

    public <T> Bundle multiReplacement(List<T> keys) {
        this.keys = keys;
        return this;
    }

    public <T, U> void addValues(List<U> val, MultiReplacementFunction<T, U> mrf) {

        for(int i = 0; i < keys.size(); i++) {
            for(U item : val) {
                String replacement = mrf.run((T) keys.get(i), item);
                if(replacement != null) {
                    if(!values.containsKey(i)) {
                        List<String> replacements = new ArrayList<>();
                        replacements.add(replacement);
                        values.put(i, replacements);
                    } else {
                        values.get(i).add(replacement);
                    }
                }
            }
            if(!values.containsKey(i)) {
                values.put(i, new ArrayList<>());
            }
        }
    }

    public Map<Integer, List<String>> getMultiReplacement() {
        return values;
    }
}
