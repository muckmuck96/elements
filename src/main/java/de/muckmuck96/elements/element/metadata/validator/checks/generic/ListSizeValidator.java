package de.muckmuck96.elements.element.metadata.validator.checks.generic;

import de.muckmuck96.elements.element.metadata.validator.MetadataValidationException;
import de.muckmuck96.elements.element.metadata.validator.MetadataValidator;
import de.muckmuck96.elements.element.metadata.validator.MetadataValueValidator;

import java.lang.reflect.Field;
import java.util.Collection;

public class ListSizeValidator implements MetadataValueValidator<Collection<?>> {

    @Override
    public void validate(
            Collection<?> value,
            Field field,
            Class<?> ownerClass,
            MetadataValidator settings
    ) throws MetadataValidationException {

        String fieldName = field.getName();
        String ownerName = ownerClass.getSimpleName();

        if (value == null) {
            if (settings.required()) {
                throw new MetadataValidationException(
                        "Field '" + fieldName + "' in " + ownerName + " must not be null."
                );
            }
            return;
        }

        int size = value.size();
        int min = settings.minSize();
        int max = settings.maxSize();

        if (size < min || size > max) {
            throw new MetadataValidationException(
                    "Size of collection field '" + fieldName + "' in " + ownerName
                            + " must be between " + min + " and " + max
                            + " (found " + size + ")."
            );
        }
    }
}

