package com.gload.core.stats;

import org.HdrHistogram.Histogram;
import java.util.concurrent.TimeUnit;

public class HistogramWrapper {
    // 1시간 범위, 유효숫자 3자리 정밀도
    private final Histogram histogram = new Histogram(TimeUnit.HOURS.toMicros(1), 3);

    public synchronized void recordValue(long latencyMs) {
        histogram.recordValue(TimeUnit.MILLISECONDS.toMicros(latencyMs));
    }

    public synchronized long getValueAtPercentile(double percentile) {
        return TimeUnit.MICROSECONDS.toMillis(histogram.getValueAtPercentile(percentile));
    }

    public synchronized long getMinValue() {
        return TimeUnit.MICROSECONDS.toMillis(histogram.getMinValue());
    }

    public synchronized long getMaxValue() {
        return TimeUnit.MICROSECONDS.toMillis(histogram.getMaxValue());
    }

    public synchronized double getMean() {
        return TimeUnit.MICROSECONDS.toMillis((long) histogram.getMean());
    }

    public synchronized void reset() {
        histogram.reset();
    }
}