package io.github.mrsaraira.constants;

/**
 * A constant that contains a value.
 *
 * @param <T> value type
 * @author Takhsin Saraira
 * @see ConstantImpl
 */
public interface Constant<T> {

    /**
     * Get contained value.
     *
     * @return contained value
     */
    T getValue();

}
