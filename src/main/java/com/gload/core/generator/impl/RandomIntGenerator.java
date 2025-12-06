package com.gload.core.generator.impl;

import com.gload.core.generator.ValueGenerator;

import java.util.concurrent.ThreadLocalRandom;

/**
 * 랜덤 정수 생성기
 * 지정된 범위 내에서 무작위 정수 생성
 */
public class RandomIntGenerator implements ValueGenerator {

    private final int min;
    private final int max;

    public RandomIntGenerator(int min, int max) {
        if (min > max) { // 같은 값일 수도 있으므로 >= 대신 > 로 변경하거나 유지 (의도에 따라)
            throw new IllegalArgumentException("min must be less than or equal to max");
        }
        this.min = min;
        this.max = max;
    }

    @Override
    public Object nextValue() {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }
}