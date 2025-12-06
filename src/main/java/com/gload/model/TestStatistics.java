package com.gload.model;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 실시간 테스트 통계
 */
public class TestStatistics {
    private long totalRequests;
    private long successCount;
    private long failCount;

    // Latency 통계
    private long minLatencyMs;
    private long maxLatencyMs;
    private double avgLatencyMs;
    private long p50LatencyMs;
    private long p95LatencyMs;
    private long p99LatencyMs;

    // 현재 상태
    private double currentRps;
    private int activeConnections;
    private long testDurationSec;
    private boolean isRunning;

    // 에러 상세
    private Map<String, Long> errorTypes;

    // 최근 에러 (최대 100개)
    private Queue<RequestMetric> recentErrors;

    // 시간대별 RPS (최근 60초)
    private Map<Long, Long> rpsHistory;

    public TestStatistics() {
        this.errorTypes = new HashMap<>();
        this.recentErrors = new ConcurrentLinkedQueue<>();
        this.rpsHistory = new LinkedHashMap<>();
        this.minLatencyMs = Long.MAX_VALUE;
        this.maxLatencyMs = 0;
    }

    // Getters and Setters
    public long getTotalRequests() { return totalRequests; }
    public void setTotalRequests(long totalRequests) { this.totalRequests = totalRequests; }

    public long getSuccessCount() { return successCount; }
    public void setSuccessCount(long successCount) { this.successCount = successCount; }

    public long getFailCount() { return failCount; }
    public void setFailCount(long failCount) { this.failCount = failCount; }

    public long getMinLatencyMs() {
        return minLatencyMs == Long.MAX_VALUE ? 0 : minLatencyMs;
    }
    public void setMinLatencyMs(long minLatencyMs) { this.minLatencyMs = minLatencyMs; }

    public long getMaxLatencyMs() { return maxLatencyMs; }
    public void setMaxLatencyMs(long maxLatencyMs) { this.maxLatencyMs = maxLatencyMs; }

    public double getAvgLatencyMs() { return avgLatencyMs; }
    public void setAvgLatencyMs(double avgLatencyMs) { this.avgLatencyMs = avgLatencyMs; }

    public long getP50LatencyMs() { return p50LatencyMs; }
    public void setP50LatencyMs(long p50LatencyMs) { this.p50LatencyMs = p50LatencyMs; }

    public long getP95LatencyMs() { return p95LatencyMs; }
    public void setP95LatencyMs(long p95LatencyMs) { this.p95LatencyMs = p95LatencyMs; }

    public long getP99LatencyMs() { return p99LatencyMs; }
    public void setP99LatencyMs(long p99LatencyMs) { this.p99LatencyMs = p99LatencyMs; }

    public double getCurrentRps() { return currentRps; }
    public void setCurrentRps(double currentRps) { this.currentRps = currentRps; }

    public int getActiveConnections() { return activeConnections; }
    public void setActiveConnections(int activeConnections) { this.activeConnections = activeConnections; }

    public long getTestDurationSec() { return testDurationSec; }
    public void setTestDurationSec(long testDurationSec) { this.testDurationSec = testDurationSec; }

    public Map<String, Long> getErrorTypes() { return errorTypes; }
    public void setErrorTypes(Map<String, Long> errorTypes) { this.errorTypes = errorTypes; }

    public Queue<RequestMetric> getRecentErrors() { return recentErrors; }
    public void setRecentErrors(Queue<RequestMetric> recentErrors) { this.recentErrors = recentErrors; }

    public Map<Long, Long> getRpsHistory() { return rpsHistory; }
    public void setRpsHistory(Map<Long, Long> rpsHistory) { this.rpsHistory = rpsHistory; }

    public boolean getIsRunning() { return isRunning; }
    public void setIsRunning(boolean running) { isRunning = running; }

    public double getSuccessRate() {
        if (totalRequests == 0) return 0.0;
        return (double) successCount / totalRequests * 100.0;
    }

    public double getErrorRate() {
        if (totalRequests == 0) return 0.0;
        return (double) failCount / totalRequests * 100.0;
    }
}
