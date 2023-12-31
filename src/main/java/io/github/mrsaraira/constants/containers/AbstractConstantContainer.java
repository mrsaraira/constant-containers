package io.github.mrsaraira.constants.containers;

import io.github.mrsaraira.constants.Constant;
import io.github.mrsaraira.constants.ConstantContainer;

import java.util.*;

/**
 * Implementation of {@link ConstantContainer} that stores constants in unmodifiable ordered Set, where similar keys are stored once.
 *
 * <br><b>Requirement:</b> The <u>constants</u> <b>must be static or static final</b> if passed to
 * {@link AbstractConstantContainer#initialConstants() initialConstants()} method from the container class fields!
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
        this.constants = Collections.unmodifiableSet(new LinkedHashSet<>(initialConstants()));
    }

    /**
     * Defines the container initial constants.
     *
     * @return list of the container constants
     */
    protected abstract List<Constant<T>> initialConstants();

    @Override
    public final Collection<Constant<T>> getAllKeys() {
        return constants;
    }

    @Override
    public final Set<T> getAllValues() {
        return ConstantContainer.super.getAllValues();
    }

}
