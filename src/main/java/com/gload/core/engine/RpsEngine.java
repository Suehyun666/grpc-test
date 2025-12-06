package com.gload.core.engine;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.LockSupport;

public class RpsEngine {

    private final ExecutorService workerExecutor;
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    private Thread dispatcherThread;
    private Runnable onFinishCallback;

    public RpsEngine(int workerThreads) {
        if (workerThreads > 0) {
            this.workerExecutor = Executors.newFixedThreadPool(workerThreads);
        } else {
            this.workerExecutor = Executors.newCachedThreadPool();
        }
    }

    public void start(int targetRps, int durationSeconds, Runnable fireRequestTask, Runnable onFinish) {
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

            System.out.printf("🚀 RPS Engine Started: Target %d RPS (interval: %.3f ms)%n",
                    targetRps, intervalNanos / 1_000_000.0);

            while (isRunning.get() && System.nanoTime() < endTime) {
                long now = System.nanoTime();

                if (now >= nextRunTime) {
                    while (now >= nextRunTime) {
                        workerExecutor.submit(fireRequestTask);
                        nextRunTime += intervalNanos;
                    }
                } else {
                    LockSupport.parkNanos(100_000);
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
