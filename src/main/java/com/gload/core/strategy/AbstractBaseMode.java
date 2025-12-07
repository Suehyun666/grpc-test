package com.gload.core.strategy;

import com.gload.core.grpc.DynamicGrpcInvoker;
import com.gload.core.grpc.GrpcClientPool;
import com.gload.core.generator.PayloadGenerator;
import com.gload.core.execution.TestModeContext;
import com.gload.core.log.TransactionLog;
import com.gload.model.TestScenario;
import com.google.protobuf.Descriptors;
import com.google.protobuf.DynamicMessage;
import io.grpc.stub.StreamObserver;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;

public abstract class AbstractBaseMode implements TestModeStrategy {
    protected final AtomicLong timerId = new AtomicLong(-1);
    protected TestModeContext context;

    protected CompletableFuture<Void> fireRequest(
            Descriptors.MethodDescriptor methodDesc,
            GrpcClientPool clientPool,
            PayloadGenerator payloadGen,
            TestModeContext context) {

        CompletableFuture<Void> future = new CompletableFuture<>();
        DynamicGrpcInvoker invoker = new DynamicGrpcInvoker(clientPool.getChannel());
        String jsonPayload = payloadGen.generateJson();
        long reqId = context.getCollector().nextRequestId();
        long start = System.currentTimeMillis();

        invoker.callAsync(methodDesc, jsonPayload, context.getTimeoutSec(), context.getMetadata(), new StreamObserver<>() {
            @Override
            public void onNext(DynamicMessage value) {
                long latency = System.currentTimeMillis() - start;

                String responseBody = null;
                try {
                    responseBody = DynamicGrpcInvoker.messageToJson(value);
                } catch (Exception e) {
                    responseBody = "Parse Error: " + e.getMessage();
                }

                context.getCollector().recordSuccess(latency, responseBody,
                        context.getServiceName(), context.getMethodName());

                context.getLogService().record(
                        TransactionLog.success(reqId, latency, responseBody)
                );

                future.complete(null);
            }

            @Override
            public void onError(Throwable t) {
                long latency = System.currentTimeMillis() - start;
                context.getCollector().recordFailure(reqId, start, latency, t,
                        context.getServiceName(), context.getMethodName());

                context.getLogService().record(
                        TransactionLog.error(reqId, latency, t.getMessage())
                );

                future.completeExceptionally(t);
            }

            @Override
            public void onCompleted() {}
        });

        return future;
    }

    @Override
    public void stop() {
        long id = timerId.get();
        if (id != -1 && context != null) {
            context.getVertx().cancelTimer(id);
            timerId.set(-1);
        }
    }

    /**
     * 시나리오에 정의된 Duration(초)만큼 실행 후 종료 신호를 보냅니다.
     * Duration이 0 이거나 없으면 무한 실행(수동 종료 필요) 또는 1회성 동작
     */
    protected void scheduleTermination(TestScenario scenario, TestModeContext context) {
        long durationSec = scenario.getLoadProfile().getDurationSec();

        if (durationSec > 0) {
            System.out.printf("⏱️ Test will run for %d seconds%n", durationSec);
            // Vert.x 타이머 설정
            context.getVertx().setTimer(durationSec * 1000, id -> {
                System.out.println("⏰ Duration over. Finishing test.");
                context.finish(); // Runner에게 종료 신호 전송
            });
        }
    }
}