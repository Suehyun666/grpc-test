package com.gload.core.strategy.impl;

import com.gload.core.grpc.GrpcClientPool;
import com.gload.core.generator.PayloadGenerator;
import com.gload.core.execution.TestModeContext;
import com.gload.model.TestScenario;
import com.gload.core.strategy.AbstractBaseMode;
import com.google.protobuf.Descriptors;

import java.util.concurrent.atomic.AtomicBoolean;

public class ConstantThroughputMode extends AbstractBaseMode {

    private final AtomicBoolean running = new AtomicBoolean(false);

    @Override
    public void execute(TestScenario scenario, Descriptors.MethodDescriptor methodDesc, GrpcClientPool clientPool, PayloadGenerator payloadGen, TestModeContext context) {
        this.context = context;
        int vUsers = scenario.getLoadProfile().getVirtualUsers();
        int targetRps = scenario.getLoadProfile().getTargetRps();

        if (vUsers <= 0) vUsers = 1;
        if (targetRps <= 0) targetRps = 1;

        double rpsPerUser = (double) targetRps / vUsers;
        long delayMs = rpsPerUser > 0 ? (long) (1000.0 / rpsPerUser) : 1000;
        if (delayMs < 1) delayMs = 1;

        running.set(true);

        System.out.printf("🎯 Start Constant Throughput Test: %d VUsers, Target %d RPS%n", vUsers, targetRps);

        for (int i = 0; i < vUsers; i++) {
            startUserLoop(methodDesc, clientPool, payloadGen, context, delayMs);
        }

        // 시간 제한 설정 (종료 시 running=false가 되어 쓰레드 루프도 멈춤)
        scheduleTermination(scenario, context);
    }

    private void startUserLoop(Descriptors.MethodDescriptor methodDesc, GrpcClientPool clientPool,
                               PayloadGenerator payloadGen, TestModeContext context, long delayMs) {
        new Thread(() -> {
            while (running.get()) {
                fireRequest(methodDesc, clientPool, payloadGen, context);
                if (delayMs > 0) {
                    try { Thread.sleep(delayMs); }
                    catch (InterruptedException e) { Thread.currentThread().interrupt(); break; }
                }
            }
        }).start();
    }

    @Override
    public void stop() {
        running.set(false);
        super.stop();
    }
}