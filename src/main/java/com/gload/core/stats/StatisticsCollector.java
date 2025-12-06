package com.gload.core.stats;

import com.gload.model.RequestMetric;
import com.gload.model.TestStatistics;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

public class StatisticsCollector {
    private final AtomicLong requestIdCounter = new AtomicLong(0);
    private final AtomicLong successCount = new AtomicLong(0);
    private final AtomicLong failCount = new AtomicLong(0);
    private final HistogramWrapper histogram = new HistogramWrapper();

    // 에러 집계
    private final Map<String, AtomicLong> errorTypes = new HashMap<>();
    private final Queue<RequestMetric> recentErrors = new ConcurrentLinkedQueue<>();

    // 성공 응답 집계
    private final Queue<RequestMetric> recentResponses = new ConcurrentLinkedQueue<>();

    // RPS 집계
    private final Map<Long, AtomicLong> rpsPerSecond = new HashMap<>();

    private final long startTime;

    public StatisticsCollector() {
        this.startTime = System.currentTimeMillis();
    }

    public long nextRequestId() { return requestIdCounter.incrementAndGet(); }

    public void recordSuccess(long latencyMs) {
        recordSuccess(latencyMs, null, null, null);
    }

    public void recordSuccess(long latencyMs, String responseData, String service, String method) {
        successCount.incrementAndGet();
        histogram.recordValue(latencyMs);
        recordRps();

        if (responseData != null) {
            long reqId = requestIdCounter.get();
            long timestamp = System.currentTimeMillis();
            RequestMetric metric = new RequestMetric(reqId, timestamp, latencyMs, true);
            metric.setResponseData(responseData);
            metric.setServiceName(service);
            metric.setMethodName(method);

            recentResponses.offer(metric);
            if (recentResponses.size() > 50) recentResponses.poll();
        }
    }

    public void recordFailure(long reqId, long timestamp, long latencyMs, Throwable t, String service, String method) {
        failCount.incrementAndGet();
        recordRps();

        String errorKey = t.getClass().getSimpleName();
        synchronized (errorTypes) {
            errorTypes.computeIfAbsent(errorKey, k -> new AtomicLong(0)).incrementAndGet();
        }

        RequestMetric metric = new RequestMetric(reqId, timestamp, latencyMs, false);
        metric.setErrorType(errorKey);
        metric.setErrorMessage(t.getMessage());
        metric.setServiceName(service);
        metric.setMethodName(method);

        recentErrors.offer(metric);
        if (recentErrors.size() > 50) recentErrors.poll(); // 메모리 보호
    }

    private void recordRps() {
        long currentSec = (System.currentTimeMillis() - startTime) / 1000;
        synchronized (rpsPerSecond) {
            rpsPerSecond.computeIfAbsent(currentSec, k -> new AtomicLong(0)).incrementAndGet();
        }
    }

    public TestStatistics getSnapshot() {
        TestStatistics stats = new TestStatistics();
        stats.setTotalRequests(requestIdCounter.get());
        stats.setSuccessCount(successCount.get());
        stats.setFailCount(failCount.get());
        stats.setTestDurationSec((System.currentTimeMillis() - startTime) / 1000);

        stats.setMinLatencyMs(histogram.getMinValue());
        stats.setMaxLatencyMs(histogram.getMaxValue());
        stats.setAvgLatencyMs(histogram.getMean());
        stats.setP50LatencyMs(histogram.getValueAtPercentile(50));
        stats.setP95LatencyMs(histogram.getValueAtPercentile(95));
        stats.setP99LatencyMs(histogram.getValueAtPercentile(99));

        synchronized (rpsPerSecond) {
            Map<Long, Long> history = new LinkedHashMap<>();
            long currentSec = stats.getTestDurationSec();
            for(long i = Math.max(0, currentSec - 60); i <= currentSec; i++) {
                history.put(i, rpsPerSecond.getOrDefault(i, new AtomicLong(0)).get());
            }
            stats.setRpsHistory(history);

            // currentRps: 현재 초만이 아니라 최근 1초간의 평균으로 계산 (초반 안정성 향상)
            long rpsNow = rpsPerSecond.getOrDefault(currentSec, new AtomicLong(0)).get();
            long rpsPrev = rpsPerSecond.getOrDefault(currentSec - 1, new AtomicLong(0)).get();
            long currentRps = currentSec < 2 ? rpsNow : (rpsNow + rpsPrev) / 2;
            stats.setCurrentRps(currentRps);
        }

        synchronized (errorTypes) {
            Map<String, Long> errors = new HashMap<>();
            errorTypes.forEach((k, v) -> errors.put(k, v.get()));
            stats.setErrorTypes(errors);
        }
        stats.setRecentErrors(new ConcurrentLinkedQueue<>(recentErrors));
        stats.setRecentResponses(new ConcurrentLinkedQueue<>(recentResponses));
        return stats;
    }
}