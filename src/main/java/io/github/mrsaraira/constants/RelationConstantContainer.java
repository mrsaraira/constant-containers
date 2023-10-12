package io.github.mrsaraira.constants;

import io.github.mrsaraira.constants.containers.AbstractRelationConstantContainer;

import java.util.Collection;

/**
 * A container that stores constants and their relations.
 * The relation constants can be operated using utility class {@link Constants} or custom logic.
 * The keys <b>have to be unique</b> as if where defined in enum class.
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
     * Get relations by key value.
     *
     * @param keyValue key value.
     * @return collection of relation as constants.
     */
    Collection<Constant<R>> getRelations(L keyValue);

    /**
     * Get all relations of the container.
     *
     * @return relation constants of the container
     */
    Collection<RelationConstant<L, R>> getRelations();

    /**
     * Get all relation values of the container.
     *
     * @return collection of relation values of the container for each key
     */
    default Collection<Collection<R>> getRelationValues() {
        return Constants.Inner.getConstantTypeRelationValues(this);
    }

}
