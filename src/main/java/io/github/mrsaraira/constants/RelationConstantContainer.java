package io.github.mrsaraira.constants;

import io.github.mrsaraira.constants.containers.AbstractRelationConstantContainer;

import java.util.Collection;
import java.util.List;

/**
 * A container that stores constants and their relations.
 * The relation constants can be operated using utility class {@link Constants} or custom logic.
 *
 * @param <L> constant keys values type
 * @param <R> relation constants value type
 * @author Takhsin Saraira
 * @see AbstractRelationConstantContainer
 * @see RelationConstant
 * @see RelationConstantImpl
 * @see Constants
 */
public interface RelationConstantContainer<L, R> extends ConstantContainer<L> {

    /**
     * Get all relation constants of the container.
     *
     * @return collection of relation constants of the container
     */
    Collection<RelationConstant<L, R>> getAllRelations();

    /**
     * Get all relation values of the relation constants of the container.
     *
     * @return list of relation values collections of the container
     */
    default List<Collection<R>> getAllRelationsValues() {
        return Constants.Inner.getAllRelationsValues(this);
    }

}
