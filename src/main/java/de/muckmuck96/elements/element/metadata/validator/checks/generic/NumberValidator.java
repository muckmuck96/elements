package de.muckmuck96.elements.element.metadata.validator.checks.generic;

import de.muckmuck96.elements.element.metadata.validator.MetadataValidationException;
import de.muckmuck96.elements.element.metadata.validator.MetadataValidator;
import de.muckmuck96.elements.element.metadata.validator.MetadataValueValidator;

import java.lang.reflect.Field;

public class NumberValidator implements MetadataValueValidator<Number> {

    @Override
    public void validate(
            Number value,
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

        double v = value.doubleValue();
        double min = settings.min();
        double max = settings.max();

        boolean tooSmall = settings.inclusiveMin() ? v < min : v <= min;
        boolean tooLarge = settings.inclusiveMax() ? v > max : v >= max;

        if (tooSmall || tooLarge) {
            String minOp = settings.inclusiveMin() ? ">=" : ">";
            String maxOp = settings.inclusiveMax() ? "<=" : "<";
            throw new MetadataValidationException(
                    "Field '" + fieldName + "' in " + ownerName + " must be "
                            + minOp + " " + min + " and " + maxOp + " " + max
                            + " (found " + v + ")."
            );
        }
    }
}


