package com.gload.model;

import java.time.LocalDateTime;

/**
 * Gatling-style Simulation 모델
 * 테스트 시나리오를 저장하고 재실행할 수 있게 하는 컨테이너
 */
public class Simulation {
    private String id;
    private String title;
    private String description;
    private TestScenario scenario;
    private LocalDateTime createdAt;
    private LocalDateTime lastRunAt;
    private int runCount;

    public Simulation() {
        this.createdAt = LocalDateTime.now();
        this.runCount = 0;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public TestScenario getScenario() { return scenario; }
    public void setScenario(TestScenario scenario) { this.scenario = scenario; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getLastRunAt() { return lastRunAt; }
    public void setLastRunAt(LocalDateTime lastRunAt) { this.lastRunAt = lastRunAt; }

    public int getRunCount() { return runCount; }
    public void setRunCount(int runCount) { this.runCount = runCount; }

    public void incrementRunCount() {
        this.runCount++;
        this.lastRunAt = LocalDateTime.now();
    }
}
