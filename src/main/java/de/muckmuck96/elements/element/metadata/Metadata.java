package de.muckmuck96.elements.element.metadata;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark classes for automatic JSON/YAML persistence.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Metadata {
    MetadataType type();

    /**
     * Optional subdirectory under the plugin's data folder.
     * "" = directly in baseDir.
     */
    String path() default "";

    /**
     * File name without extension.
     *
     * - If empty -> class simple name is used.
     * - If it contains "{id}" -> this storage is treated as a pattern storage
     *   and you MUST use the get/save overloads that take an id.
     */
    String name() default "";

    /**
     * If false, this storage will never be saved via DataHandler.saveMetadatas().
     * Useful for configs you want to be read-only from code.
     */
    boolean editable() default true;

    /**
     * If true, when loading existing files, missing fields in the file
     * are filled from the class's default values and optionally written back.
     */
    boolean autoMigrate() default true;

    /**
     * If true, this storage is treated as a "config":
     * - DataHandler will check the file's lastModified on each get(...)
     * - If changed, it reloads & migrates before returning.
     * If false, it's a normal cached storage (no auto reload).
     */
    boolean config() default false;
}
