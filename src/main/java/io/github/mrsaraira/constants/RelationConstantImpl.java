package io.github.mrsaraira.constants;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

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
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
class RelationConstantImpl<L, R> implements RelationConstant<L, R> {

    @Getter
    private final Constant<L> key;
    private final Collection<Constant<R>> relations;

    @Override
    public L getValue() {
        return getKey().getValue();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Constant<R>[] getRelations() {
        return Constants.toArray(relations, new Constant[0]);
    }

}
