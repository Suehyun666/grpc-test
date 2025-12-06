package com.gload.core.log;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.Vertx;
import io.vertx.core.file.AsyncFile;
import io.vertx.core.file.OpenOptions;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@ApplicationScoped
public class SimulationLogService {

    @Inject Vertx vertx;

    private final ObjectMapper mapper = new ObjectMapper();
    private final Queue<TransactionLog> buffer = new ConcurrentLinkedQueue<>();
    private AsyncFile asyncFile;
    private final AtomicBoolean isRecording = new AtomicBoolean(false);
    private long flushTimerId = -1;

    public enum LogLevel { ALL, ERRORS_ONLY, NONE }
    private LogLevel currentLevel = LogLevel.ERRORS_ONLY;

    public void startLogging(String simulationId, LogLevel level) {
        // 기존 실행 중인 로깅이 있다면 안전하게 종료
        stopLogging();

        this.currentLevel = level;
        if (level == LogLevel.NONE) return;

        String fileName = "logs/simulation_" + simulationId + ".jsonl";

        // 폴더 생성 (Blocking 방지를 위해 vertx api 권장하나, 초기화 시점이라 java.io도 무방)
        new java.io.File("logs").mkdirs();

        vertx.fileSystem().open(fileName, new OpenOptions().setAppend(true).setCreate(true), res -> {
            if (res.succeeded()) {
                this.asyncFile = res.result();
                this.isRecording.set(true);

                this.flushTimerId = vertx.setPeriodic(1000, id -> flushBuffer());
                System.out.println("📝 Logging started: " + fileName);
            } else {
                System.err.println("❌ Failed to open log file: " + res.cause().getMessage());
            }
        });
    }

    public void record(TransactionLog log) {
        if (!isRecording.get()) return;

        if (currentLevel == LogLevel.ERRORS_ONLY && "OK".equals(log.getStatus())) {
            return;
        }

        buffer.offer(log);
    }

    private void flushBuffer() {
        // asyncFile이 null이면 쓰기 불가능
        if (buffer.isEmpty() || asyncFile == null) return;

        StringBuilder chunk = new StringBuilder();
        TransactionLog item;

        int batchSize = 0;
        while ((item = buffer.poll()) != null && batchSize < 1000) {
            try {
                chunk.append(mapper.writeValueAsString(item)).append("\n");
                batchSize++;
            } catch (Exception e) {
                // Ignore serialization error
            }
        }

        if (chunk.length() > 0) {
            try {
                // 비동기 쓰기 시도. 파일이 닫혀있으면 예외가 발생할 수 있음.
                io.vertx.core.buffer.Buffer vertxBuffer = io.vertx.core.buffer.Buffer.buffer(chunk.toString());
                asyncFile.write(vertxBuffer);
            } catch (Exception e) {
                System.err.println("Failed to write to log file (maybe closed): " + e.getMessage());
            }
        }
    }

    public synchronized void stopLogging() {
        // [Fix] compareAndSet을 사용하여 중복 호출 방지
        if (isRecording.compareAndSet(true, false)) {

            if (flushTimerId != -1) {
                vertx.cancelTimer(flushTimerId);
                flushTimerId = -1;
            }

            // 남은 버퍼 쓰기
            flushBuffer();

            // [Fix] 파일 핸들 닫고 반드시 null 처리
            if (asyncFile != null) {
                try {
                    asyncFile.close();
                } catch (Exception e) {
                    // 이미 닫혀있다면 무시
                } finally {
                    asyncFile = null; // 중요: 참조 제거하여 재호출 시 close() 방지
                }
                System.out.println("📝 Logging stopped.");
            }
        }
    }
}