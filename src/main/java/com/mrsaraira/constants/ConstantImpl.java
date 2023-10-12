package com.mrsaraira.constants;

import lombok.Value;

@Value
class ConstantImpl<T> implements Constant<T> {

    T value;

    @Override
    public T getValue() {
        return value;
    }

}
