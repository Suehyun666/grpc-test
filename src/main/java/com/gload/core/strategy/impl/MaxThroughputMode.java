package com.gload.core.strategy.impl;

import com.gload.core.grpc.GrpcClientPool;
import com.gload.core.generator.PayloadGenerator;
import com.gload.core.execution.TestModeContext;
import com.gload.model.TestScenario;
import com.gload.core.strategy.AbstractBaseMode;
import com.google.protobuf.Descriptors;
import java.util.concurrent.atomic.AtomicBoolean;

public class MaxThroughputMode extends AbstractBaseMode {

    private final AtomicBoolean running = new AtomicBoolean(false);

    @Override
    public void execute(TestScenario scenario, Descriptors.MethodDescriptor methodDesc, GrpcClientPool clientPool, PayloadGenerator payloadGen, TestModeContext context) {
        this.context = context;
        int vUsers = scenario.getLoadProfile().getVirtualUsers();
        long timeoutSec = scenario.getTimeoutSec();
        running.set(true);

        System.out.printf("🔥 Start Max Throughput Test: %d Virtual Users%n", vUsers);

        for (int i = 0; i < vUsers; i++) {
            startUserLoop(methodDesc, clientPool, payloadGen, context, timeoutSec);
        }

        scheduleTermination(scenario, context);
    }

    private void startUserLoop(Descriptors.MethodDescriptor methodDesc, GrpcClientPool clientPool, PayloadGenerator payloadGen, TestModeContext context, long timeoutSec) {
        new Thread(() -> {
            while (running.get()) {
                fireRequest(methodDesc, clientPool, payloadGen, context);
            }
        }).start();
    }

    @Override
    public void stop() {
        running.set(false);
        super.stop();
    }
}