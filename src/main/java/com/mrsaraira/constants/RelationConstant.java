package com.mrsaraira.constants;

import java.util.Collection;

public interface RelationConstant<L, R> extends Constant<L> {

    Constant<L> getKey();

    Collection<Constant<R>> getRelations();

}
