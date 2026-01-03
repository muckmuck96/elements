package de.muckmuck96.elements.element.metadata.validator.checks.generic;

import de.muckmuck96.elements.element.metadata.validator.MetadataValidationException;
import de.muckmuck96.elements.element.metadata.validator.MetadataValidator;
import de.muckmuck96.elements.element.metadata.validator.MetadataValueValidator;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;

public class EnumListValidator implements MetadataValueValidator<List<?>> {

    @Override
    public void validate(
            List<?> value,
            Field field,
            Class<?> ownerClass,
            MetadataValidator settings
    ) throws MetadataValidationException {

        if (value == null) {
            if (settings.required()) {
                throw new MetadataValidationException(
                        "Enum list field '" + field.getName() + "' in "
                                + ownerClass.getSimpleName() + " must not be null."
                );
            }
            return;
        }

        boolean hasNull = value.stream().anyMatch(Objects::isNull);
        if (hasNull) {
            throw new MetadataValidationException(
                    "Invalid enum value in list field '" + field.getName() + "' of "
                            + ownerClass.getSimpleName()
                            + ". One or more entries could not be parsed."
            );
        }

        boolean nonEnum = value.stream()
                .filter(Objects::nonNull)
                .anyMatch(e -> !(e instanceof Enum<?>));

        if (nonEnum) {
            throw new MetadataValidationException(
                    "Field '" + field.getName() + "' of " + ownerClass.getSimpleName()
                            + " is annotated with EnumListValidator but contains non-enum values."
            );
        }
    }
}


