package com.gload.core.strategy.impl;

import com.gload.core.grpc.GrpcClientPool;
import com.gload.core.generator.PayloadGenerator;
import com.gload.core.execution.TestModeContext;
import com.gload.core.strategy.AbstractBaseMode;
import com.gload.core.engine.BurstEngine;
import com.gload.model.TestScenario;
import com.google.protobuf.Descriptors;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class BurstMode extends AbstractBaseMode {

    private BurstEngine burstEngine;

    @Override
    public void execute(TestScenario scenario, Descriptors.MethodDescriptor methodDesc, GrpcClientPool clientPool, PayloadGenerator payloadGen, TestModeContext context) {
        this.context = context;

        int burstSize = scenario.getLoadProfile().getTargetRps();

        if (burstSize <= 0) burstSize = 100;

        System.out.printf("💥 Burst Mode: %d concurrent users firing simultaneously%n", burstSize);

        burstEngine = new BurstEngine();

        Supplier<CompletableFuture<Void>> fireTask = () -> fireRequest(methodDesc, clientPool, payloadGen, context);
        Runnable onComplete = context::finish;

        burstEngine.fireBurst(burstSize, fireTask, onComplete);
    }

    @Override
    public void stop() {
        if (burstEngine != null) {
            burstEngine.stop();
        }
        super.stop();
    }
}
