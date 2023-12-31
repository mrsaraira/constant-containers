package io.github.mrsaraira.constants.containers;

import io.github.mrsaraira.constants.Constant;
import io.github.mrsaraira.constants.RelationConstant;
import io.github.mrsaraira.constants.RelationConstantContainer;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Implementation of {@link RelationConstantContainer} that stores constants in unmodifiable ordered Map.
 * The keys relations might have same values for different keys.
 * The keys ordered as initial constants list.
 * <br><b>Requirement:</b> The <u>constants</u> <b>must be static or static final</b> if passed to
 * {@link AbstractConstantContainer#initialConstants() initialConstants()} method from the container class fields!
 * <br><b>Requirement2:</b> The keys <b>must be unique</b> as if they were defined in enum class.
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

    protected final Map<Constant<L>, RelationConstant<L, R>> constantsMap;

    protected AbstractRelationConstantContainer() {
        var constantsMap =
                initialConstants()
                        .stream()
                        .collect(Collectors.toMap(RelationConstant::getKey, Function.identity(),
                                (constants, constants2) -> {
                                    throw new IllegalArgumentException("Duplicated keys were found");
                                },
                                LinkedHashMap::new));
        this.constantsMap = Collections.unmodifiableMap(constantsMap);
    }

    /**
     * Defines the container initial relation constants.
     *
     * @return list of the container relation constants
     */
    protected abstract List<RelationConstant<L, R>> initialConstants();

    @Override
    public final Collection<Constant<L>> getAllKeys() {
        return constantsMap.keySet();
    }

    @Override
    public final Collection<RelationConstant<L, R>> getAllRelations() {
        return constantsMap.values();
    }

    @Override
    public final Set<L> getAllValues() {
        return RelationConstantContainer.super.getAllValues();
    }

    @Override
    public final List<Collection<R>> getAllRelationsValues() {
        return RelationConstantContainer.super.getAllRelationsValues();
    }

}
