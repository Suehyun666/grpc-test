package com.gload.core.strategy.impl;

import com.gload.core.strategy.AbstractBaseMode;
import com.gload.core.grpc.GrpcClientPool;
import com.gload.core.generator.PayloadGenerator;
import com.gload.core.execution.TestModeContext;
import com.gload.model.TestScenario;
import com.google.protobuf.Descriptors;

public class LoadTestMode extends AbstractBaseMode {
    @Override
    public void execute(TestScenario scenario, Descriptors.MethodDescriptor methodDesc, GrpcClientPool clientPool, PayloadGenerator payloadGen, TestModeContext context) {
        this.context = context;
        int targetRps = scenario.getLoadProfile().getTargetRps();
        long periodMs = targetRps > 0 ? 1000 / targetRps : 1000;
        if (periodMs < 1) periodMs = 1;

        timerId.set(context.getVertx().setPeriodic(periodMs, id -> {
            fireRequest(methodDesc, clientPool, payloadGen, context);
        }));

        scheduleTermination(scenario, context);
    }
}