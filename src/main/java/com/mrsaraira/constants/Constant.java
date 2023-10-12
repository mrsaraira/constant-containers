package com.mrsaraira.constants;

import lombok.Value;

@Value
class Constant<T> implements IConstant<T> {

    T value;

    @Override
    public T getValue() {
        return value;
    }

}
