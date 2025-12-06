package com.gload.core.execution;

import com.gload.core.generator.impl.CsvFeederGenerator;
import com.gload.core.generator.PayloadGenerator;
import com.gload.core.generator.ValueGenerator;
import com.gload.core.generator.impl.RandomIntGenerator;
import com.gload.core.generator.impl.RoundRobinGenerator;
import com.gload.core.generator.impl.SequenceGenerator;
import com.gload.core.generator.impl.UuidGenerator;
import com.gload.core.grpc.GrpcClientPool;
import com.gload.core.log.SimulationLogService;
import com.gload.core.stats.StatisticsCollector;
import com.gload.model.Mode;
import com.gload.model.TestScenario;
import com.gload.model.TestStatistics;
import com.gload.core.grpc.ProtoDescriptorLoader;
import com.gload.core.strategy.TestModeStrategy;
import com.gload.core.strategy.impl.*;
import com.google.protobuf.Descriptors;
import io.vertx.core.Vertx;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.io.IOException;
import java.util.*;
import java.util.UUID;

@ApplicationScoped
public class ScenarioRunner {

    @Inject Vertx vertx;
    @Inject SimulationLogService logService;

    private StatisticsCollector collector;
    private GrpcClientPool clientPool;
    private TestModeStrategy currentStrategy;
    private String currentSimulationId;

    public void start(TestScenario scenario, Descriptors.FileDescriptor fd) throws IOException {
        start(scenario, java.util.List.of(fd));
    }

    public void start(TestScenario scenario, java.util.List<Descriptors.FileDescriptor> fds) throws IOException {
        stop();
        collector = new StatisticsCollector();

        currentSimulationId = "sim_" + System.currentTimeMillis();
        SimulationLogService.LogLevel logLevel =
                (scenario.getLoadProfile().getMode() == Mode.SINGLE)
                        ? SimulationLogService.LogLevel.ALL
                        : SimulationLogService.LogLevel.ERRORS_ONLY;

        logService.startLogging(currentSimulationId, logLevel);

        Descriptors.MethodDescriptor methodDesc = ProtoDescriptorLoader.findMethod(
                fds, scenario.getServiceName(), scenario.getMethodName());

        String[] parts = scenario.getEndpoint().split(":");
        clientPool = new GrpcClientPool(
                parts[0],
                Integer.parseInt(parts[1]),
                scenario.getLoadProfile().getVirtualUsers(),
                scenario.isUseTls(),           // TLS 사용 여부
                scenario.getTlsCertPath(),     // 인증서 경로
                scenario.getLoadProfile().getWorkerThreads() // 워커 스레드 수
        );

        Map<String, ValueGenerator> generators = new HashMap<>();
        if (scenario.getFieldRules() != null) {
            for (Map.Entry<String, TestScenario.FieldRule> entry : scenario.getFieldRules().entrySet()) {
                generators.put(entry.getKey(), createGeneratorFromRule(entry.getValue()));
            }
        }
        if (generators.isEmpty()) {
            generators.put("message", () -> "Hello-" + System.currentTimeMillis());
        }

        PayloadGenerator payloadGen = new PayloadGenerator(generators);

        // Context 생성 및 종료 핸들러 등록
        TestModeContext context = new TestModeContext(
                vertx, collector, logService,
                scenario.getServiceName(),
                scenario.getMethodName(),
                scenario.getTimeoutSec(),
                scenario.getMetadata()
        );

        // [중요] Strategy가 finish()를 호출하면 Runner를 stop() 시킨다.
        context.setOnFinishHandler(() -> {
            System.out.println("✅ Test finished by strategy signal.");
            this.stop();
        });

        currentStrategy = createStrategy(scenario.getLoadProfile().getMode());
        currentStrategy.execute(scenario, methodDesc, clientPool, payloadGen, context);
    }

    private TestModeStrategy createStrategy(Mode mode) {
        switch (mode) {
            case SINGLE: return new SingleMode();
            case CONSTANT_THROUGHPUT: return new ConstantThroughputMode();
            case MAX_THROUGHPUT: return new MaxThroughputMode();
            case BURST: return new BurstMode();
            case LOAD_TEST: return new LoadTestMode();
            default: return new SingleMode();
        }
    }

    public synchronized void stop() {
        if (currentStrategy != null) {
            currentStrategy.stop();
            currentStrategy = null;
        }
        if (clientPool != null) {
            clientPool.shutdown();
            clientPool = null;
        }
        logService.stopLogging();
    }

    public Map<String, Long> getStats() {
        if (collector == null) return Map.of("total", 0L);
        TestStatistics stats = collector.getSnapshot();
        return Map.of("total", stats.getTotalRequests(), "success", stats.getSuccessCount(), "fail", stats.getFailCount());
    }

    public java.util.concurrent.CompletableFuture<String> getSingleResultFuture() {
        if (currentStrategy instanceof SingleMode) {
            return ((SingleMode) currentStrategy).getResponseFuture();
        }
        return java.util.concurrent.CompletableFuture.failedFuture(new IllegalStateException("Not in SINGLE mode"));
    }

    private ValueGenerator createGeneratorFromRule(TestScenario.FieldRule rule) throws IOException {
        switch (rule.getType()) {
            case FIXED: return () -> rule.getValue();
            case RANDOM_INT: return new RandomIntGenerator(
                    rule.getMinValue() != null ? rule.getMinValue() : 0,
                    rule.getMaxValue() != null ? rule.getMaxValue() : Integer.MAX_VALUE);
            case UUID: return new UuidGenerator();
            case SEQUENCE: return new SequenceGenerator(
                    rule.getMinValue() != null ? rule.getMinValue() : 1,
                    rule.getMaxValue() != null ? rule.getMaxValue() : Integer.MAX_VALUE);
            case RANDOM_STRING: return () -> "STR-" + UUID.randomUUID().toString().substring(0, 8);
            case CSV_FEEDER: return rule.getCsvValues() != null ?
                    new CsvFeederGenerator(rule.getCsvValues()) : new CsvFeederGenerator(rule.getCsvFilePath());
            case ROUND_ROBIN:
                // Round Robin: minValue와 maxValue 범위로 생성 (예: 1000-2000)
                int start = rule.getMinValue() != null ? rule.getMinValue() : 1;
                int end = rule.getMaxValue() != null ? rule.getMaxValue() : 100;
                return new RoundRobinGenerator(start, end);
            default: return () -> "default-" + System.currentTimeMillis();
        }
    }

    public TestStatistics getDetailedStats() {
        if (collector == null) {
            return new TestStatistics();
        }
        TestStatistics stats = collector.getSnapshot();
        stats.setIsRunning(isRunning());
        return stats;
    }

    public StatisticsCollector getCollector() {
        return collector;
    }
    public boolean isRunning() {
        return currentStrategy != null;
    }
}