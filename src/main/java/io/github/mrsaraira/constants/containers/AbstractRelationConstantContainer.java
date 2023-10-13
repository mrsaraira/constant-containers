package io.github.mrsaraira.constants.containers;

import io.github.mrsaraira.constants.Constant;
import io.github.mrsaraira.constants.Constants;
import io.github.mrsaraira.constants.RelationConstant;
import io.github.mrsaraira.constants.RelationConstantContainer;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of {@link RelationConstantContainer} that stores constants in {@link java.util.HashMap}.
 * The keys relations might have same values for different keys.
 * <br><b>Requirement:</b> The inheritor class must have no-args constructor!
 * <p>
 * {@inheritDoc}
 *
 * @param <L> constant keys values type
 * @param <R> relation constants values type
 * @author Takhsin Saraira
 * @see RelationConstantContainer
 * @see RelationConstant
 */
public abstract class AbstractRelationConstantContainer<L, R> implements RelationConstantContainer<L, R> {

    protected final Map<Constant<L>, Collection<Constant<R>>> constantsMap;

    protected AbstractRelationConstantContainer() {
        this.constantsMap = initialConstants()
                .stream()
                .collect(Collectors.toUnmodifiableMap(RelationConstant::getKey, RelationConstant::getRelations));
    }

    /**
     * Defines the container initial relation constants.
     *
     * @return list of the container relation constants
     */
    protected abstract List<RelationConstant<L, R>> initialConstants();

    @Override
    public final Optional<Constant<L>> getKey(L value) {
        return getKeys().stream().filter(constant -> Objects.equals(constant.getValue(), value)).findFirst();
    }

    @Override
    public final Collection<Constant<L>> getKeys() {
        return constantsMap.keySet();
    }

    @Override
    public final Collection<Constant<R>> getRelations(L keyValue) {
        return constantsMap.get(Constants.of(keyValue));
    }

    @Override
    public final Collection<RelationConstant<L, R>> getRelations() {
        return initialConstants();
    }

    @Override
    public final Collection<L> getKeyValues() {
        return RelationConstantContainer.super.getKeyValues();
    }

    @Override
    public final Collection<Collection<R>> getRelationValues() {
        return RelationConstantContainer.super.getRelationValues();
    }

}
