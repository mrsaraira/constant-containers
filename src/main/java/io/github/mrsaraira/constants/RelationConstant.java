package io.github.mrsaraira.constants;

import java.util.Collection;

/**
 * A constant that contains a key and its relations.
 *
 * @param <L> key type
 * @param <R> relations type
 * @author Takhsin Saraira
 * @see Constant
 * @see ConstantImpl
 * @see RelationConstantImpl
 */
public interface RelationConstant<L, R> extends Constant<L> {

    /**
     * Get the key constant.
     *
     * @return constant key
     */
    Constant<L> getKey();

    /**
     * Get relations as constants.
     *
     * @return constant relations
     */
    Collection<Constant<R>> getRelations();

}
