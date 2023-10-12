package com.mrsaraira.constants;

import lombok.Value;

import java.util.Collection;

@Value
class RelationConstant<L, R> implements IRelationConstant<L, R> {

    IConstant<L> key;
    Collection<IConstant<R>> relations;

    @Override
    public L getValue() {
        return key.getValue();
    }

}
