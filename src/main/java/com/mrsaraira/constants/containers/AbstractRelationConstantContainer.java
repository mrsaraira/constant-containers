package com.mrsaraira.constants.containers;

import com.mrsaraira.constants.Constants;
import com.mrsaraira.constants.IConstant;
import com.mrsaraira.constants.IRelationConstant;
import com.mrsaraira.constants.RelationConstantContainer;

import java.util.*;
import java.util.stream.Collectors;

public abstract class AbstractRelationConstantContainer<T, R> implements RelationConstantContainer<T, R> {

    protected final Map<IConstant<T>, Collection<IConstant<R>>> constantsMap;

    protected AbstractRelationConstantContainer() {
        this.constantsMap = initialConstants()
                .stream()
                .collect(Collectors.toUnmodifiableMap(IRelationConstant::getKey, IRelationConstant::getRelations));
    }

    protected abstract List<IRelationConstant<T, R>> initialConstants();

    @Override
    public final Optional<IConstant<T>> getKey(T value) {
        return getKeys().stream().filter(constant -> Objects.equals(constant.getValue(), value)).findFirst();
    }

    @Override
    public final Collection<IConstant<T>> getKeys() {
        return constantsMap.keySet();
    }

    @Override
    public final Collection<IConstant<R>> getRelations(T key) {
        return constantsMap.get(Constants.of(key));
    }

    @Override
    public final Collection<T> getKeyValues() {
        return RelationConstantContainer.super.getKeyValues();
    }

    @Override
    public final Collection<Collection<R>> getRelationValues() {
        return RelationConstantContainer.super.getRelationValues();
    }

    @Override
    public final Collection<IRelationConstant<T, R>> getRelations() {
        return initialConstants();
    }

}
