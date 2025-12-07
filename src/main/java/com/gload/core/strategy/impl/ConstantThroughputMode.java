package com.gload.core.strategy.impl;

import com.gload.core.grpc.GrpcClientPool;
import com.gload.core.generator.PayloadGenerator;
import com.gload.core.execution.TestModeContext;
import com.gload.model.TestScenario;
import com.gload.core.strategy.AbstractBaseMode;
import com.gload.core.engine.RpsEngine;
import com.google.protobuf.Descriptors;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class ConstantThroughputMode extends AbstractBaseMode {

    private RpsEngine rpsEngine;

    @Override
    public void execute(TestScenario scenario, Descriptors.MethodDescriptor methodDesc, GrpcClientPool clientPool, PayloadGenerator payloadGen, TestModeContext context) {
        this.context = context;
        int targetRps = scenario.getLoadProfile().getTargetRps();
        int durationSec = (int) scenario.getLoadProfile().getDurationSec();
        int workerThreads = scenario.getLoadProfile().getWorkerThreads();

        if (targetRps <= 0) targetRps = 1;
        if (durationSec <= 0) durationSec = 60;
        if (workerThreads <= 0) workerThreads = Math.max(4, Math.min(targetRps / 100, 32));

        System.out.printf("🎯 Constant Throughput Mode: Target %d RPS, Duration %d sec, Worker Threads %d%n",
                targetRps, durationSec, workerThreads);

        rpsEngine = new RpsEngine(workerThreads);

        Supplier<CompletableFuture<Void>> fireTask = () -> fireRequest(methodDesc, clientPool, payloadGen, context);
        Runnable onFinish = context::finish;

        rpsEngine.start(targetRps, durationSec, fireTask, onFinish);
    }

    @Override
    public void stop() {
        if (rpsEngine != null) {
            rpsEngine.stop();
        }
        super.stop();
    }
}