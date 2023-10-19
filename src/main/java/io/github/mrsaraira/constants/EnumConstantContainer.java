package io.github.mrsaraira.constants;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Enumeration constant container that stores a {@link Constant}.
 * The constants can be operated using utility class {@link Constants} or custom logic.
 * <p>
 * {@inheritDoc}
 *
 * @param <L> relation constant key value type
 * @param <E> (self reference) of Enum which implements this {@link EnumConstantContainer}
 * @author Takhsin Saraira
 */
public interface EnumConstantContainer<L, E extends Enum<E> & EnumConstantContainer<L, E>> extends ConstantContainer<L> {

    /**
     * Get enumeration constant.
     *
     * @return enumeration constant
     */
    Constant<L> getConstant();

    @Override
    default Collection<Constant<L>> getAllKeys() {
        return Arrays.stream(values()).map(EnumConstantContainer::getConstant).collect(Collectors.toUnmodifiableSet());
    }

    @Override
    default Set<L> getAllValues() {
        return getAllKeys().stream()
                .map(Constant::getValue)
                .collect(Collectors.toUnmodifiableSet());
    }

    @SuppressWarnings("unchecked")
    private E[] values() {
        return Constants.Inner.getEnumValues((Class<E>) getClass());
    }

}
