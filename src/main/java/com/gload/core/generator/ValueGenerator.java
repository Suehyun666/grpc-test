package com.gload.core.generator;

/**
 * 값 생성기 인터페이스
 * 각 필드마다 다른 규칙(랜덤, 순차, 고정 등)으로 값을 생성
 */
public interface ValueGenerator {

    /**
     * 다음 값 생성
     */
    Object nextValue();

    /**
     * 생성기 초기화 (필요한 경우)
     */
    default void reset() {}
}