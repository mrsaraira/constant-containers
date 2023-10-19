package io.github.mrsaraira.constants;

import io.github.mrsaraira.constants.containers.AbstractConstantContainer;

import java.util.Collection;
import java.util.Set;

/**
 * A container that stores constants with the same contained value type (the key value).
 * The constants can be operated using utility class {@link Constants} or custom logic.
 *
 * @param <T> constants key type
 * @author Takhsin Saraira
 * @see AbstractConstantContainer
 * @see Constant
 * @see ConstantImpl
 * @see Constants
 */
public interface ConstantContainer<T> {

    /**
     * Get constant keys.
     *
     * @return constant keys
     */
    Collection<Constant<T>> getAllKeys();

    /**
     * Get all values of the container.
     *
     * @return all values of the container
     */
    default Set<T> getAllValues() {
        return Constants.Inner.getAllConstantsValues(this);
    }

}
