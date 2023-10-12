package com.mrsaraira.constants;

import java.util.Collection;

public interface IRelationConstant<L, R> extends IConstant<L> {

    IConstant<L> getKey();

    Collection<IConstant<R>> getRelations();

}
