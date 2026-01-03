package de.muckmuck96.elements.element.metadata.validator.checks.generic;

import de.muckmuck96.elements.element.metadata.validator.MetadataValidationException;
import de.muckmuck96.elements.element.metadata.validator.MetadataValidator;
import de.muckmuck96.elements.element.metadata.validator.MetadataValueValidator;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class MapValidator implements MetadataValueValidator<Map<?, ?>> {

    @Override
    public void validate(
            Map<?, ?> value,
            Field field,
            Class<?> ownerClass,
            MetadataValidator settings
    ) throws MetadataValidationException {

        String fieldName = field.getName();
        String ownerName = ownerClass.getSimpleName();

        if (value == null) {
            if (settings.required()) {
                throw new MetadataValidationException(
                        "Map field '" + fieldName + "' in " + ownerName + " must not be null."
                );
            }
            return;
        }

        int size = value.size();
        int min = settings.minSize();
        int max = settings.maxSize();

        if (size < min || size > max) {
            throw new MetadataValidationException(
                    "Size of map field '" + fieldName + "' in " + ownerName
                            + " must be between " + min + " and " + max
                            + " (found " + size + ")."
            );
        }

        String[] allowedKeysArr = settings.allowedKeys();
        if (allowedKeysArr.length > 0) {
            Set<String> allowed = Arrays.stream(allowedKeysArr).collect(Collectors.toSet());

            for (Object key : value.keySet()) {
                if (!(key instanceof String k)) {
                    throw new MetadataValidationException(
                            "Map field '" + fieldName + "' in " + ownerName
                                    + " has non-String key: " + key
                    );
                }
                if (!allowed.contains(k)) {
                    throw new MetadataValidationException(
                            "Map field '" + fieldName + "' in " + ownerName
                                    + " has unsupported key: '" + k
                                    + "'. Allowed keys are: " + allowed
                    );
                }
            }
        }
    }
}

