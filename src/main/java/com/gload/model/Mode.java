package com.gload.model;

public enum Mode {
    SINGLE,               // 단일 요청
    CONSTANT_THROUGHPUT,  // 일정한 처리량 유지 (VUser + Target RPS)
    MAX_THROUGHPUT,       // 최대 처리량 유지
    LOAD_TEST,           // 일반 부하 테스트
    SOAK_TEST,           // 장시간 부하 테스트
    SPIKE,                // 급격한 부하 증가
    RAMP_UP,              // 점진적 부하 증가
    BURST                 // 순간 폭발적 부하
}
