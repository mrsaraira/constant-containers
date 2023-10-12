package com.mrsaraira.constants;

import java.util.Collection;
import java.util.Optional;

public interface ConstantContainer<T> {

    Optional<Constant<T>> getKey(T value);

    Collection<Constant<T>> getKeys();

    @SuppressWarnings("unchecked")
    private Class<ConstantContainer<T>> getType() {
        return (Class<ConstantContainer<T>>) getClass();
    }

    default Collection<T> getKeyValues() {
        return Constants.Inner.getConstantTypeKeys(this);
    }

}
