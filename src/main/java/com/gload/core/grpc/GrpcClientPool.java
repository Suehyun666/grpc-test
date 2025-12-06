package com.gload.core.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class GrpcClientPool {

    private final List<ManagedChannel> channels;
    private final ExecutorService channelExecutor; // [추가] 커스텀 스레드 풀
    private int currentIndex = 0;

    public GrpcClientPool(String host, int port, int poolSize, boolean useTls, String certPath, int workerThreads) {

        this.channels = new ArrayList<>(poolSize);

        if (workerThreads > 0) {
            this.channelExecutor = Executors.newFixedThreadPool(workerThreads);
        } else {
            this.channelExecutor = null;
        }

        for (int i = 0; i < poolSize; i++) {
            ManagedChannelBuilder<?> builder = ManagedChannelBuilder.forAddress(host, port);

            // 1. TLS 설정 적용
            if (useTls) {
                builder.useTransportSecurity();
                // 참고: 커스텀 인증서(certPath)를 로드하려면 NettyChannelBuilder와 GrpcSslContexts가 필요합니다.
                // 현재 기본 ManagedChannelBuilder에서는 시스템 기본 인증서를 사용합니다.
                if (certPath != null && !certPath.isEmpty()) {
                    System.out.println("⚠️ Warning: Custom cert path provided but standard builder uses system roots. Use NettyChannelBuilder for custom certs.");
                }
            } else {
                builder.usePlaintext();
            }

            // 2. Worker Threads (Executor) 적용
            if (this.channelExecutor != null) {
                builder.executor(this.channelExecutor);
            }

            channels.add(builder.build());
        }

        System.out.printf("✓ Created %d gRPC channels to %s:%d (TLS: %s, Threads: %d)%n",
                poolSize, host, port, useTls, workerThreads);
    }

    public synchronized ManagedChannel getChannel() {
        ManagedChannel channel = channels.get(currentIndex);
        currentIndex = (currentIndex + 1) % channels.size();
        return channel;
    }

    public void shutdown() {
        for (ManagedChannel channel : channels) {
            try {
                channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                channel.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }

        if (channelExecutor != null) {
            channelExecutor.shutdownNow();
        }
        System.out.println("✓ All gRPC channels and executors closed");
    }

    public int getPoolSize() {
        return channels.size();
    }
}