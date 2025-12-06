package com.gload.model;

/**
 * 부하 테스트 프로필 설정
 * 사용자가 UI에서 입력하는 부하 조건을 담는 모델
 */
public class LoadProfile {

    private Mode mode;
    private int virtualUsers;      // 동시 연결 클라이언트 수
    private int durationSec;       // 테스트 진행 시간 (초)
    private int targetRps;         // 초당 목표 요청 수
    private int rampUpSec;         // 점진적 증가 시간 (RAMP_UP 모드용)
    private int workerThreads;     // Netty EventLoop 스레드 수

    public LoadProfile() {
        // 기본값 설정
        this.mode = Mode.SINGLE;
        this.virtualUsers = 1;
        this.durationSec = 60;
        this.targetRps = 10;
        this.rampUpSec = 0;
        this.workerThreads = Runtime.getRuntime().availableProcessors() * 2;
    }

    // Getters and Setters
    public Mode getMode() { return mode; }
    public void setMode(Mode mode) { this.mode = mode; }

    public int getVirtualUsers() { return virtualUsers; }
    public void setVirtualUsers(int virtualUsers) { this.virtualUsers = virtualUsers; }

    public int getDurationSec() { return durationSec; }
    public void setDurationSec(int durationSec) { this.durationSec = durationSec; }

    public int getTargetRps() { return targetRps; }
    public void setTargetRps(int targetRps) { this.targetRps = targetRps; }

    public int getRampUpSec() { return rampUpSec; }
    public void setRampUpSec(int rampUpSec) { this.rampUpSec = rampUpSec; }

    public int getWorkerThreads() { return workerThreads; }
    public void setWorkerThreads(int workerThreads) { this.workerThreads = workerThreads; }

    @Override
    public String toString() {
        return String.format("LoadProfile{mode=%s, vUsers=%d, duration=%ds, rps=%d}",
            mode, virtualUsers, durationSec, targetRps);
    }
}
