package de.muckmuck96.elements.element.metadata.validator.checks.generic;

import de.muckmuck96.elements.element.metadata.validator.MetadataValidationException;
import de.muckmuck96.elements.element.metadata.validator.MetadataValidator;
import de.muckmuck96.elements.element.metadata.validator.MetadataValueValidator;

import java.lang.reflect.Field;
import java.util.regex.Pattern;

public class StringValidator implements MetadataValueValidator<String> {

    @Override
    public void validate(
            String value,
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

        int len = value.length();
        if (len < settings.minSize() || len > settings.maxSize()) {
            throw new MetadataValidationException(
                    "Length of field '" + fieldName + "' in " + ownerName
                            + " must be between " + settings.minSize() + " and "
                            + settings.maxSize() + " (found " + len + ")."
            );
        }

        String regex = settings.regex();
        if (!regex.isEmpty()) {
            Pattern pattern = Pattern.compile(regex);
            if (!pattern.matcher(value).matches()) {
                throw new MetadataValidationException(
                        "Field '" + fieldName + "' in " + ownerName
                                + " does not match required pattern: " + regex
                );
            }
        }
    }
}

