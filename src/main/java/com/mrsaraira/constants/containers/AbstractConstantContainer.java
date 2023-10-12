package com.mrsaraira.constants.containers;

import com.mrsaraira.constants.ConstantContainer;
import com.mrsaraira.constants.IConstant;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class AbstractConstantContainer<T> implements ConstantContainer<T> {

    protected final Collection<IConstant<T>> constants;

    protected AbstractConstantContainer() {
        this.constants = initialConstants()
                .stream()
                .collect(Collectors.toUnmodifiableSet());
    }

    protected abstract List<IConstant<T>> initialConstants();

    @Override
    public final Optional<IConstant<T>> getKey(T value) {
        return constants.stream().filter(constant -> Objects.equals(constant.getValue(), value)).findFirst();
    }

    @Override
    public final Collection<IConstant<T>> getKeys() {
        return constants;
    }

    @Override
    public final Collection<T> getKeyValues() {
        return ConstantContainer.super.getKeyValues();
    }

}
