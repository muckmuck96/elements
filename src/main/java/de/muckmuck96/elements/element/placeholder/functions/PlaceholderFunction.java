package de.muckmuck96.elements.element.placeholder.functions;

import de.muckmuck96.elements.element.placeholder.classes.Bundle;

/**
 * Function that generates replacement values for placeholders.
 *
 * @param <T> the return type of the placeholder value
 */
@FunctionalInterface
public interface PlaceholderFunction<T> {
    T run(Bundle bundle);
}
