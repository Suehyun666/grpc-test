package com.gload.core.strategy.impl;

import com.gload.core.grpc.DynamicGrpcInvoker;
import com.gload.core.grpc.GrpcClientPool;
import com.gload.core.generator.PayloadGenerator;
import com.gload.core.execution.TestModeContext;
import com.gload.core.log.TransactionLog;
import com.gload.core.strategy.AbstractBaseMode;
import com.gload.model.TestScenario;
import com.google.protobuf.Descriptors;
import com.google.protobuf.DynamicMessage;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicBoolean;

public class BurstMode extends AbstractBaseMode {

    @Override
    public void execute(TestScenario scenario, Descriptors.MethodDescriptor methodDesc, GrpcClientPool clientPool, PayloadGenerator payloadGen, TestModeContext context) {
        this.context = context;

        // 프론트엔드에서 'Burst Size'로 입력받은 값 (Target RPS 필드 재사용)
        int burstSize = scenario.getLoadProfile().getTargetRps();

        // 각 요청이 최대로 기다릴 시간 (Timeout)
        long timeoutSec = scenario.getTimeoutSec();
        if (timeoutSec <= 0) timeoutSec = 5; // 기본값 5초

        // Duration 가져오기
        long durationSec = scenario.getLoadProfile().getDurationSec();

        System.out.printf("💥 FIRE! Sending %d requests simultaneously (timeout: %ds, duration: %ds)...%n",
            burstSize, timeoutSec, durationSec);

        // 미완료 요청 수 추적
        AtomicInteger pendingRequests = new AtomicInteger(burstSize);
        AtomicBoolean finished = new AtomicBoolean(false);

        // 모든 요청을 보낸 후, 응답이 돌아올 때까지 대기할 시간 계산
        long waitTimeMs = (timeoutSec * 1000) + 2000;
        if (durationSec > 0) {
            long durationMs = durationSec * 1000;
            waitTimeMs = Math.min(waitTimeMs, durationMs);
        }

        final long maxWaitTimeMs = waitTimeMs;

        // [핵심] 타이머 없이 루프만 돌려서 순식간에 요청을 쏟아냄 (비동기)
        for (int i = 0; i < burstSize; i++) {
            DynamicGrpcInvoker invoker = new DynamicGrpcInvoker(clientPool.getChannel());
            String jsonPayload = payloadGen.generateJson();
            long reqId = context.getCollector().nextRequestId();
            long start = System.currentTimeMillis();

            invoker.callAsync(methodDesc, jsonPayload, context.getTimeoutSec(), context.getMetadata(), new StreamObserver<>() {
                @Override
                public void onNext(DynamicMessage value) {
                    long latency = System.currentTimeMillis() - start;
                    context.getCollector().recordSuccess(latency);

                    String responseBody = null;
                    try {
                        responseBody = DynamicGrpcInvoker.messageToJson(value);
                    } catch (Exception e) {
                        responseBody = "Parse Error: " + e.getMessage();
                    }

                    context.getLogService().record(
                            TransactionLog.success(reqId, latency, responseBody)
                    );

                    // 요청 완료 체크
                    checkCompletion();
                }

                @Override
                public void onError(Throwable t) {
                    long latency = System.currentTimeMillis() - start;
                    context.getCollector().recordFailure(reqId, start, latency, t,
                            context.getServiceName(), context.getMethodName());

                    context.getLogService().record(
                            TransactionLog.error(reqId, latency, t.getMessage())
                    );

                    // 요청 완료 체크
                    checkCompletion();
                }

                @Override
                public void onCompleted() {}

                private void checkCompletion() {
                    int remaining = pendingRequests.decrementAndGet();
                    if (remaining == 0 && !finished.getAndSet(true)) {
                        System.out.println("✅ All burst requests completed!");
                        context.finish();
                    }
                }
            });
        }

        // 최대 대기 시간 후 강제 종료 (타임아웃 방지)
        timerId.set(context.getVertx().setTimer(maxWaitTimeMs, id -> {
            if (!finished.getAndSet(true)) {
                System.out.printf("⏰ Burst max wait time (%d ms) reached. Finishing...%n", maxWaitTimeMs);
                context.finish();
            }
        }));
    }
}