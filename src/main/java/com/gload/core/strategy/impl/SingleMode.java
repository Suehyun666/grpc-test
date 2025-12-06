package com.gload.core.strategy.impl;

import com.gload.core.strategy.AbstractBaseMode;
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

public class SingleMode extends AbstractBaseMode {

    private final CompletableFuture<String> responseFuture = new CompletableFuture<>();

    public CompletableFuture<String> getResponseFuture() {
        return responseFuture;
    }

    @Override
    public void execute(TestScenario scenario, Descriptors.MethodDescriptor methodDesc, GrpcClientPool clientPool, PayloadGenerator payloadGen, TestModeContext context) {
        this.context = context;
        DynamicGrpcInvoker invoker = new DynamicGrpcInvoker(clientPool.getChannel());
        String jsonPayload = payloadGen.generateJson();
        long reqId = context.getCollector().nextRequestId();
        long start = System.currentTimeMillis();

        invoker.callAsync(methodDesc, jsonPayload, new StreamObserver<>() {
            @Override
            public void onNext(DynamicMessage value) {
                long latency = System.currentTimeMillis() - start;
                context.getCollector().recordSuccess(latency);

                String responseBody = null;
                try {
                    responseBody = DynamicGrpcInvoker.messageToJson(value);
                    responseFuture.complete(responseBody);
                } catch (Exception e) {
                    responseBody = "Parse Error: " + e.getMessage();
                    responseFuture.completeExceptionally(new RuntimeException("Failed to convert response: " + e.getMessage(), e));
                }
                context.getLogService().record(TransactionLog.success(reqId, latency, responseBody));

                // [종료 신호] 응답 성공 시 종료
                context.finish();
            }

            @Override
            public void onError(Throwable t) {
                long latency = System.currentTimeMillis() - start;
                context.getCollector().recordFailure(reqId, start, latency, t, context.getServiceName(), context.getMethodName());
                responseFuture.completeExceptionally(t);
                context.getLogService().record(TransactionLog.error(reqId, latency, t.getMessage()));

                // [종료 신호] 에러 발생 시에도 종료
                context.finish();
            }

            @Override
            public void onCompleted() {}
        });
    }
}