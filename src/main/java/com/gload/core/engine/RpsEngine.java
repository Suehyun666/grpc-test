package com.gload.core.engine;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.LockSupport;
import java.util.function.Supplier;

public class RpsEngine {

    private final ExecutorService workerExecutor;
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    private Thread dispatcherThread;
    private Runnable onFinishCallback;
    private final boolean useVirtualThreads;

    public RpsEngine(int workerThreads) {
        this.useVirtualThreads = isVirtualThreadsSupported();

        if (useVirtualThreads) {
            this.workerExecutor = createVirtualThreadExecutor();
            System.out.println("✨ Using Virtual Threads for high performance");
        } else {
            if (workerThreads > 0) {
                this.workerExecutor = Executors.newFixedThreadPool(workerThreads);
            } else {
                this.workerExecutor = Executors.newCachedThreadPool();
            }
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

    public void start(int targetRps, int durationSeconds, Supplier<CompletableFuture<Void>> fireRequestTask, Runnable onFinish) {
        if (isRunning.get()) {
            throw new IllegalStateException("RpsEngine is already running");
        }

        this.onFinishCallback = onFinish;
        isRunning.set(true);

        dispatcherThread = new Thread(() -> {
            long intervalNanos = 1_000_000_000L / targetRps;
            long startTime = System.nanoTime();
            long nextRunTime = startTime;
            long endTime = durationSeconds > 0 ? startTime + (durationSeconds * 1_000_000_000L) : Long.MAX_VALUE;

            boolean useSpinWait = targetRps >= 5000;

            System.out.printf("🚀 RPS Engine Started: Target %d RPS (interval: %.3f ms, %s)%n",
                    targetRps, intervalNanos / 1_000_000.0,
                    useSpinWait ? "Spin-Wait" : "Park-Wait");

            while (isRunning.get() && System.nanoTime() < endTime) {
                long now = System.nanoTime();

                if (now >= nextRunTime) {
                    while (now >= nextRunTime) {
                        workerExecutor.submit(() -> fireRequestTask.get());
                        nextRunTime += intervalNanos;
                    }
                } else {
                    if (useSpinWait) {
                        Thread.onSpinWait();
                    } else {
                        LockSupport.parkNanos(1_000);
                    }
                }
            }

            System.out.println("🏁 RPS Engine Finished");
            isRunning.set(false);

            if (onFinishCallback != null) {
                onFinishCallback.run();
            }

        }, "RPS-Dispatcher-Thread");

        dispatcherThread.start();
    }

    public void stop() {
        isRunning.set(false);
        if (dispatcherThread != null) {
            dispatcherThread.interrupt();
        }
        workerExecutor.shutdown();
    }

    public boolean isRunning() {
        return isRunning.get();
    }
}
