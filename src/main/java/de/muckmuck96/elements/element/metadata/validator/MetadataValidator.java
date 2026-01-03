package de.muckmuck96.elements.element.metadata.validator;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface MetadataValidator {
    Class<? extends MetadataValueValidator<?>> value();

    // --- Generic numeric config (NumberValidator) ---
    double min() default Double.NEGATIVE_INFINITY;
    double max() default Double.POSITIVE_INFINITY;
    boolean inclusiveMin() default true;
    boolean inclusiveMax() default true;
    boolean required() default false; // also used by other validators for null-check

    // --- String config (StringValidator) ---
    String regex() default "";      // optional pattern constraint
    int minSize() default 0;        // min length for String or Collection/Map
    int maxSize() default Integer.MAX_VALUE;

    // --- Map config (MapValidator) ---
    String[] allowedKeys() default {};

    // --- Composite config (CompositeValidator) ---
    Class<? extends MetadataValueValidator<?>>[] delegates() default {};
}
