package io.github.mrsaraira.constants;

/**
 * A constant with value.
 *
 * @param <T> value type
 * @author Takhsin Saraira
 * @see ConstantImpl
 */
public interface Constant<T> {

    /**
     * Get the constant value.
     *
     * @return constant value
     */
    T getValue();

}
