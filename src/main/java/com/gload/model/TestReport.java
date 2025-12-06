package com.gload.model;

import java.time.LocalDateTime;

/**
 * Gatling-style Test Report
 * 테스트 실행 결과 요약본
 */
public class TestReport {
    private String reportId;
    private String simulationId;
    private String simulationTitle;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private long durationMs;

    private TestStatistics statistics;

    // 요약 정보
    private String status; // SUCCESS, FAILED, STOPPED
    private String summary;

    public TestReport() {
        this.startTime = LocalDateTime.now();
    }

    public void finish(String status) {
        this.endTime = LocalDateTime.now();
        this.status = status;
        if (startTime != null && endTime != null) {
            this.durationMs = java.time.Duration.between(startTime, endTime).toMillis();
        }
    }

    // Getters and Setters
    public String getReportId() { return reportId; }
    public void setReportId(String reportId) { this.reportId = reportId; }

    public String getSimulationId() { return simulationId; }
    public void setSimulationId(String simulationId) { this.simulationId = simulationId; }

    public String getSimulationTitle() { return simulationTitle; }
    public void setSimulationTitle(String simulationTitle) { this.simulationTitle = simulationTitle; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public long getDurationMs() { return durationMs; }
    public void setDurationMs(long durationMs) { this.durationMs = durationMs; }

    public TestStatistics getStatistics() { return statistics; }
    public void setStatistics(TestStatistics statistics) { this.statistics = statistics; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
}
