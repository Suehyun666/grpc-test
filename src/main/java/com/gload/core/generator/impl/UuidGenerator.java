package com.gload.core.generator.impl;

import com.gload.core.generator.ValueGenerator;

import java.util.UUID;

/**
 * UUID 생성기
 */
public class UuidGenerator implements ValueGenerator {

    @Override
    public Object nextValue() {
        return UUID.randomUUID().toString();
    }
}
