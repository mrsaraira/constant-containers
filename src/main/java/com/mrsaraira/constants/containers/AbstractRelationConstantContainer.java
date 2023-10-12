package com.mrsaraira.constants.containers;

import com.mrsaraira.constants.Constants;
import com.mrsaraira.constants.Constant;
import com.mrsaraira.constants.RelationConstant;
import com.mrsaraira.constants.RelationConstantContainer;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class AbstractRelationConstantContainer<T, R> implements RelationConstantContainer<T, R> {

    protected final Map<Constant<T>, Collection<Constant<R>>> constantsMap;

    protected AbstractRelationConstantContainer() {
        this.constantsMap = initialConstants()
                .stream()
                .collect(Collectors.toUnmodifiableMap(RelationConstant::getKey, RelationConstant::getRelations));
    }

    protected abstract List<RelationConstant<T, R>> initialConstants();

    @Override
    public final Optional<Constant<T>> getKey(T value) {
        return getKeys().stream().filter(constant -> Objects.equals(constant.getValue(), value)).findFirst();
    }

    @Override
    public final Collection<Constant<T>> getKeys() {
        return constantsMap.keySet();
    }

    @Override
    public final Collection<Constant<R>> getRelations(T key) {
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
    public final Collection<RelationConstant<T, R>> getRelations() {
        return initialConstants();
    }

}
