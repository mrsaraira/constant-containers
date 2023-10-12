package com.mrsaraira.constants;

import java.util.Collection;

/**
 * A constant that contains a key and it's relations.
 * The keys <b>have to be unique</b> as if where defined in enum class.
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
     * Get constant key.
     *
     * @return constant key
     */
    Constant<L> getKey();

    /**
     * Get constant relations.
     *
     * @return constant relations
     */
    Collection<Constant<R>> getRelations();

}
