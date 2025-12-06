package com.gload.core.strategy;

import com.gload.core.grpc.GrpcClientPool;
import com.gload.core.generator.PayloadGenerator;
import com.gload.core.execution.TestModeContext;
import com.gload.model.TestScenario;
import com.google.protobuf.Descriptors;

/**
 * 테스트 모드별 실행 전략 인터페이스
 */
public interface TestModeStrategy {

    /**
     * 테스트 실행
     */
    void execute(
        TestScenario scenario,
        Descriptors.MethodDescriptor methodDesc,
        GrpcClientPool clientPool,
        PayloadGenerator payloadGen,
        TestModeContext context
    );

    /**
     * 테스트 중지
     */
    void stop();
}
