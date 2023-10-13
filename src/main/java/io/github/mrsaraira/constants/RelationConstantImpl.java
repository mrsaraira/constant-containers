package io.github.mrsaraira.constants;

import lombok.Value;

import java.util.Collection;

/**
 * Immutable implementation of {@link RelationConstant}. Its value is equal to its key value.
 * <p>
 * {@inheritDoc}
 *
 * @param <L> key value type
 * @param <R> relations type
 * @author Takhsin Saraira
 */
@Value
class RelationConstantImpl<L, R> implements RelationConstant<L, R> {

    Constant<L> key;
    Collection<Constant<R>> relations;

    @Override
    public L getValue() {
        return getKey().getValue();
    }

}
