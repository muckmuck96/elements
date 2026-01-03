package de.muckmuck96.elements.element.metadata.validator.checks.generic;

import de.muckmuck96.elements.element.metadata.validator.MetadataValidationException;
import de.muckmuck96.elements.element.metadata.validator.MetadataValidator;
import de.muckmuck96.elements.element.metadata.validator.MetadataValueValidator;

import java.lang.reflect.Field;

public class CompositeValidator implements MetadataValueValidator<Object> {

    @Override
    public void validate(
            Object value,
            Field field,
            Class<?> ownerClass,
            MetadataValidator settings
    ) throws MetadataValidationException {

        Class<? extends MetadataValueValidator<?>>[] delegates = settings.delegates();
        if (delegates.length == 0) {
            // nothing to do
            return;
        }

        for (Class<? extends MetadataValueValidator<?>> delegateClass : delegates) {
            MetadataValueValidator<?> delegate;
            try {
                delegate = delegateClass.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new MetadataValidationException(
                        "Failed to instantiate delegate validator "
                                + delegateClass.getName() + " for field '"
                                + field.getName() + "' in "
                                + ownerClass.getSimpleName()
                );
            }

            @SuppressWarnings("unchecked")
            MetadataValueValidator<Object> typed = (MetadataValueValidator<Object>) delegate;

            // Reuse the same @MetadataValidator settings for all delegates
            typed.validate(value, field, ownerClass, settings);
        }
    }
}

