package com.gload.core.execution;

import com.gload.core.log.SimulationLogService;
import com.gload.core.stats.StatisticsCollector;
import io.vertx.core.Vertx;

import java.util.Map;

public class TestModeContext {
    private final Vertx vertx;
    private final StatisticsCollector collector;
    private final SimulationLogService logService;
    private final String serviceName;
    private final String methodName;
    private final long timeoutSec;
    private final Map<String, String> metadata;
    private Runnable onFinishCallback;

    public TestModeContext(
            Vertx vertx,
            StatisticsCollector collector,
            SimulationLogService logService,
            String serviceName,
            String methodName,
            long timeoutSec,
            Map<String, String> metadata
    ) {
        this.vertx = vertx;
        this.collector = collector;
        this.logService = logService;
        this.serviceName = serviceName;
        this.methodName = methodName;
        this.timeoutSec = timeoutSec;
        this.metadata = metadata != null ? metadata : new java.util.HashMap<>();
    }

    public Vertx getVertx() { return vertx; }
    public StatisticsCollector getCollector() { return collector; }
    public SimulationLogService getLogService() { return logService; }
    public String getServiceName() { return serviceName; }
    public String getMethodName() { return methodName; }
    public long getTimeoutSec() { return timeoutSec; }
    public Map<String, String> getMetadata() { return metadata; }
    public void setOnFinishHandler(Runnable callback) {this.onFinishCallback = callback;}
    public void finish() {
        if (onFinishCallback != null) {
            onFinishCallback.run();
        }
    }
}