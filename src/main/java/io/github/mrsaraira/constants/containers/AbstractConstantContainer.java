package io.github.mrsaraira.constants.containers;

import io.github.mrsaraira.constants.Constant;
import io.github.mrsaraira.constants.ConstantContainer;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of {@link ConstantContainer} that stores constants in {@link java.util.Set}.
 * If keys have same value, then only one key will be stored.
 * <br><b>Requirement:</b> The inheritor class must have no-args constructor!
 * <p>
 * {@inheritDoc}
 *
 * @param <T> constant keys values type
 * @author Takhsin Saraira
 * @see ConstantContainer
 * @see Constant
 */
public abstract class AbstractConstantContainer<T> implements ConstantContainer<T> {

    protected final Collection<Constant<T>> constants;

    protected AbstractConstantContainer() {
        this.constants = initialConstants()
                .stream()
                .collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Defines the container initial constants.
     *
     * @return list of the container constants
     */
    protected abstract List<Constant<T>> initialConstants();

    @Override
    public final Optional<Constant<T>> getKey(T value) {
        return constants.stream().filter(constant -> Objects.equals(constant.getValue(), value)).findFirst();
    }

    @Override
    public final Collection<Constant<T>> getKeys() {
        return constants;
    }

    @Override
    public final Collection<T> getKeyValues() {
        return ConstantContainer.super.getKeyValues();
    }

}
