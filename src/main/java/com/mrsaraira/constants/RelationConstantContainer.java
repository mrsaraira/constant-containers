package com.mrsaraira.constants;

import java.util.Collection;

public interface RelationConstantContainer<L, R> extends ConstantContainer<L> {

    Collection<Constant<R>> getRelations(L key);

    Collection<RelationConstant<L, R>> getRelations();

    @SuppressWarnings("unchecked")
    private Class<RelationConstantContainer<L, R>> getType() {
        return (Class<RelationConstantContainer<L, R>>) getClass();
    }

    default Collection<Collection<R>> getRelationValues() {
        return Constants.Inner.getConstantTypeRelationValues(this);
    }

}
