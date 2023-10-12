package com.mrsaraira.constants;

import java.util.Collection;
import java.util.Optional;

/**
 * A container that stores constants with the same contained value type (the key value).
 * The constants can be operated using utility class {@link Constants} or custom logic.
 *
 * @param <T> constants key type
 * @author Takhsin Saraira
 * @see com.mrsaraira.constants.containers.AbstractConstantContainer
 * @see Constant
 * @see ConstantImpl
 * @see Constants
 */
public interface ConstantContainer<T> {

    /**
     * Get constant key by value.
     *
     * @param value contained key value
     * @return constant optional
     */
    Optional<Constant<T>> getKey(T value);

    /**
     * Get constant keys.
     *
     * @return constant keys
     */
    Collection<Constant<T>> getKeys();

    /**
     * Get all key values of the container.
     *
     * @return key values of the container
     */
    default Collection<T> getKeyValues() {
        return Constants.Inner.getConstantTypeKeys(this);
    }

}
