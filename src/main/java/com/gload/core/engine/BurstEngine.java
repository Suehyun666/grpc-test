package com.gload.core.engine;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

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

    public void fireBurst(int concurrentUsers, Runnable fireRequestTask, Runnable onComplete) {
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

    private void runSingleBurst(int userCount, Runnable fireRequestTask) throws InterruptedException {
        CountDownLatch readyLatch = new CountDownLatch(userCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(userCount);

        System.out.println("💣 Burst Setup: " + userCount + " concurrent users");

        for (int i = 0; i < userCount; i++) {
            executorService.submit(() -> {
                try {
                    readyLatch.countDown();
                    startLatch.await();

                    fireRequestTask.run();

                    doneLatch.countDown();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    doneLatch.countDown();
                } catch (Exception e) {
                    doneLatch.countDown();
                }
            });
        }

        readyLatch.await();
        System.out.println("🔫 All users ready... 3, 2, 1, FIRE!");

        long startTime = System.nanoTime();
        startLatch.countDown();

        doneLatch.await();
        long endTime = System.nanoTime();

        System.out.printf("✅ Burst Finished in %.2f ms%n", (endTime - startTime) / 1_000_000.0);
    }

    public void stop() {
        isRunning.set(false);
        executorService.shutdown();
    }

    public boolean isRunning() {
        return isRunning.get();
    }
}
