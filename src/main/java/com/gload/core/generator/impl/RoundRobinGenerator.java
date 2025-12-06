package com.gload.core.generator.impl;

import com.gload.core.generator.ValueGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Round Robin 생성기
 * 주어진 리스트에서 순환하며 값을 반환
 *
 * 사용 예:
 * - 1000명의 vuser에게 1000-2000 범위의 accountId를 순환 할당
 * - vuser 1: 1000, vuser 2: 1001, ..., vuser 1001: 1000 (순환)
 */
public class RoundRobinGenerator implements ValueGenerator {

    private final List<Object> values;
    private final AtomicLong counter;

    public RoundRobinGenerator(List<Object> values) {
        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException("Values list cannot be empty");
        }
        this.values = new ArrayList<>(values);
        this.counter = new AtomicLong(0);
    }

    /**
     * 숫자 범위로 생성 (예: 1000-2000)
     */
    public RoundRobinGenerator(int start, int end) {
        this.values = new ArrayList<>();
        for (int i = start; i <= end; i++) {
            this.values.add(i);
        }
        this.counter = new AtomicLong(0);
    }

    @Override
    public Object nextValue() {
        long index = counter.getAndIncrement() % values.size();
        return values.get((int) index);
    }

    @Override
    public void reset() {
        counter.set(0);
    }
}
