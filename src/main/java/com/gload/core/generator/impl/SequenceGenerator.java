package com.gload.core.generator.impl;

import com.gload.core.generator.ValueGenerator;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 순차 증가 생성기
 * 스레드 안전한 순차 번호 생성 (범위 순환 지원)
 */
public class SequenceGenerator implements ValueGenerator {

    private final AtomicLong counter;
    private final long start;
    private final long end;
    private final long step;

    public SequenceGenerator(long start, long end, long step) {
        this.start = start;
        this.end = end;
        this.step = step;
        this.counter = new AtomicLong(start);
    }

    public SequenceGenerator(long start, long end) {
        this(start, end, 1);
    }

    public SequenceGenerator() {
        this(1, Long.MAX_VALUE, 1);
    }

    @Override
    public Object nextValue() {
        long current = counter.getAndAdd(step);

        // 범위를 넘으면 다시 시작점으로 순환
        if (current > end) {
            counter.set(start);
            return start;
        }

        return current;
    }

    @Override
    public void reset() {
        counter.set(start);
    }
}
