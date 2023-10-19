package io.github.mrsaraira.constants;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Enumeration relation constant container that stores a {@link RelationConstant}.
 * The relation constants can be operated using utility class {@link Constants} or custom logic.
 * <p>
 * {@inheritDoc}
 *
 * @param <L>         relation constant key value type
 * @param <R>relation constant relation values type
 * @param <E>         (self reference) of Enum which implements this {@link EnumRelationConstantContainer}
 * @author Takhsin Saraira
 */
public interface EnumRelationConstantContainer<L, R, E extends Enum<E> & EnumRelationConstantContainer<L, R, E>> extends EnumConstantContainer<L, E>, RelationConstantContainer<L, R> {

    /**
     * Get enumeration relation constant.
     *
     * @return enum relation constant
     */
    RelationConstant<L, R> getConstant();

    /**
     * Returns relation values of the enumeration relation constant.
     *
     * @return collection of relation values of the constant.
     */
    default Collection<R> getRelationValues() {
        return Constants.getValues(getConstant().getRelations());
    }

    @Override
    default Collection<RelationConstant<L, R>> getAllRelations() {
        return Arrays.stream(values()).map(EnumRelationConstantContainer::getConstant).collect(Collectors.toUnmodifiableSet());
    }

    @Override
    default List<Collection<R>> getAllRelationsValues() {
        return getAllRelations().stream()
                .map(RelationConstant::getRelations)
                .map(Constants::getValues)
                .collect(Collectors.toUnmodifiableList());
    }

    @SuppressWarnings("unchecked")
    private E[] values() {
        return Constants.Inner.getEnumValues((Class<E>) getClass());
    }

}
