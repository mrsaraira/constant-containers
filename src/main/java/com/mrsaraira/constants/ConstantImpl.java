package com.mrsaraira.constants;

import lombok.Value;

/**
 * Immutable implementation of {@link Constant}.
 * <p>
 * {@inheritDoc}
 *
 * @param <T> contained value type
 * @author Takhsin Saraira
 * @see Constant
 */
@Value
class ConstantImpl<T> implements Constant<T> {

    T value;

    @Override
    public T getValue() {
        return value;
    }

}
