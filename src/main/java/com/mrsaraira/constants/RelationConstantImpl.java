package com.mrsaraira.constants;

import lombok.Value;

import java.util.Collection;

@Value
class RelationConstantImpl<L, R> implements RelationConstant<L, R> {

    Constant<L> key;
    Collection<Constant<R>> relations;

    @Override
    public L getValue() {
        return key.getValue();
    }

}
