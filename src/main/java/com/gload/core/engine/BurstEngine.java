package com.gload.core.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class BurstEngine {

    private final ExecutorService executorService;
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    private final boolean useVirtualThreads;

    public BurstEngine() {
        this.useVirtualThreads = isVirtualThreadsSupported();

        if (useVirtualThreads) {
            this.executorService = createVirtualThreadExecutor();
            System.out.println("✨ Using Virtual Threads for Burst Mode");
        } else {
            this.executorService = Executors.newCachedThreadPool();
        }
    }

    private boolean isVirtualThreadsSupported() {
        try {
            Class.forName("java.lang.Thread$Builder");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    private ExecutorService createVirtualThreadExecutor() {
        try {
            var method = Executors.class.getMethod("newVirtualThreadPerTaskExecutor");
            return (ExecutorService) method.invoke(null);
        } catch (Exception e) {
            return Executors.newCachedThreadPool();
        }
    }

    public void fireBurst(int concurrentUsers, Supplier<CompletableFuture<Void>> fireRequestTask, Runnable onComplete) {
        if (isRunning.get()) {
            throw new IllegalStateException("BurstEngine is already running");
        }

        isRunning.set(true);

        new Thread(() -> {
            try {
                runSingleBurst(concurrentUsers, fireRequestTask);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                isRunning.set(false);
                if (onComplete != null) {
                    onComplete.run();
                }
            }
        }, "Burst-Coordinator-Thread").start();
    }

    private void runSingleBurst(int userCount, Supplier<CompletableFuture<Void>> fireRequestTask) throws InterruptedException {
        CountDownLatch readyLatch = new CountDownLatch(userCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        List<CompletableFuture<Void>> futures = new CopyOnWriteArrayList<>();

        System.out.println("💣 Burst Setup: " + userCount + " concurrent users");

        for (int i = 0; i < userCount; i++) {
            executorService.submit(() -> {
                try {
                    readyLatch.countDown();
                    startLatch.await();

                    CompletableFuture<Void> future = fireRequestTask.get();
                    futures.add(future);

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        readyLatch.await();
        System.out.println("🔫 All users ready... 3, 2, 1, FIRE!");

        long startTime = System.nanoTime();
        startLatch.countDown();

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(
            futures.toArray(new CompletableFuture[0])
        );

        try {
            allFutures.get();
        } catch (ExecutionException e) {
            System.err.println("⚠️ Some requests failed: " + e.getMessage());
        }

        long endTime = System.nanoTime();

        System.out.printf("✅ Burst Finished: %d requests completed in %.2f ms%n",
                userCount, (endTime - startTime) / 1_000_000.0);
    }

    public void stop() {
        isRunning.set(false);
        executorService.shutdown();
    }

    public boolean isRunning() {
        return isRunning.get();
    }
}
