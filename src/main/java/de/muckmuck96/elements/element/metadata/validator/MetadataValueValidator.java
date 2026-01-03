package de.muckmuck96.elements.element.metadata.validator;

import java.lang.reflect.Field;

public interface MetadataValueValidator<T> {
    /**
     * @param value      the value of the field after loading
     * @param field      the field itself
     * @param ownerClass the class containing the field
     * @param settings   the @MetadataValidator annotation used on this field
     */
    void validate(T value, Field field, Class<?> ownerClass, MetadataValidator settings)
            throws MetadataValidationException;
}
