package com.gload.core.generator.impl;

import com.gload.core.generator.ValueGenerator;

/**
 * 고정값 생성기
 */
public class FixedValueGenerator implements ValueGenerator {

    private final Object value;

    public FixedValueGenerator(Object value) {
        this.value = value;
    }

    @Override
    public Object nextValue() {
        return value;
    }
}
