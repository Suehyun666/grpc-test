package com.gload.core.strategy.impl;

import com.gload.core.grpc.GrpcClientPool;
import com.gload.core.generator.PayloadGenerator;
import com.gload.core.execution.TestModeContext;
import com.gload.core.strategy.AbstractBaseMode;
import com.gload.model.TestScenario;
import com.google.protobuf.Descriptors;

public class RampUpMode extends AbstractBaseMode {

    @Override
    public void execute(TestScenario scenario, Descriptors.MethodDescriptor methodDesc, GrpcClientPool clientPool, PayloadGenerator payloadGen, TestModeContext context) {
        this.context = context;
        int targetRps = scenario.getLoadProfile().getTargetRps();
        int rampUpSec = scenario.getLoadProfile().getRampUpSec();
        long startTime = System.currentTimeMillis();

        System.out.printf("🚀 Start Ramp-Up Test: Target %d RPS over %d sec%n", targetRps, rampUpSec);

        timerId.set(context.getVertx().setPeriodic(100, id -> {
            long elapsedMillis = System.currentTimeMillis() - startTime;
            double progress = (double) elapsedMillis / (rampUpSec * 1000);

            int currentRps = (int) (targetRps * Math.min(progress, 1.0));
            if (currentRps < 1) currentRps = 1;

            int requestsToFire = currentRps / 10;
            if (requestsToFire < 1) requestsToFire = 1;

            for (int i = 0; i < requestsToFire; i++) {
                fireRequest(methodDesc, clientPool, payloadGen, context);
            }
        }));
    }

    @Override
    public void stop() {
        System.out.println("🛑 Ramp-Up Test Stopped");
        super.stop();
    }
}