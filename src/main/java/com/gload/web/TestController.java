package com.gload.web;

import com.gload.core.execution.ScenarioRunner;
import com.gload.model.Mode;
import com.gload.model.TestScenario;
import com.gload.model.TestStatistics;
import com.gload.core.grpc.DynamicProtoCompiler;
import com.gload.core.grpc.ProtoDescriptorLoader;
import com.google.protobuf.Descriptors;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TestController {

    @Inject ScenarioRunner runner;

    private Descriptors.FileDescriptor currentFd;

    @POST
    @Path("/start")
    public Response startTest(TestScenario scenario) {
        try {
            File protoFile = new File(scenario.getProtoFilePath());

            if (!protoFile.exists()) {
                return Response.status(400)
                    .entity(Map.of("status", "error", "message", "Proto file not found: " + scenario.getProtoFilePath()))
                    .build();
            }

            File descFile;
            if (protoFile.getName().endsWith(".proto")) {
                descFile = DynamicProtoCompiler.compileProtoToDesc(protoFile.toPath());
            } else if (protoFile.getName().endsWith(".grpc")) {
                descFile = DynamicProtoCompiler.compileProtoToDesc(protoFile.toPath());
            } else {
                descFile = protoFile;
            }

            currentFd = ProtoDescriptorLoader.load(descFile.getAbsolutePath());

            runner.start(scenario, currentFd);

            if (scenario.getLoadProfile().getMode() == Mode.SINGLE) {
                try {
                    String result = runner.getSingleResultFuture().get(10, TimeUnit.SECONDS);
                    TestStatistics stats = runner.getDetailedStats();

                    runner.stop();

                    Map<String, Object> responseMap = new HashMap<>();
                    responseMap.put("status", "completed");
                    responseMap.put("mode", "SINGLE");
                    responseMap.put("response", result);
                    responseMap.put("stats", stats);

                    return Response.ok(responseMap).build();

                } catch (TimeoutException e) {
                    runner.stop();
                    return Response.status(504)
                        .entity(Map.of("status", "error", "message", "Single request timed out"))
                        .build();
                } catch (Exception e) {
                    runner.stop();
                    return Response.status(500)
                        .entity(Map.of("status", "error", "message", "Request failed: " + e.getMessage()))
                        .build();
                }
            } else {
                Map<String, Object> responseMap = new HashMap<>();
                responseMap.put("status", "started");
                responseMap.put("mode", scenario.getLoadProfile().getMode().toString());
                responseMap.put("message", "Load test started in background.");

                return Response.ok(responseMap).build();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500)
                .entity(Map.of("status", "error", "message", e.getMessage()))
                .build();
        }
    }

    @POST
    @Path("/stop")
    public Map<String, String> stopTest() {
        runner.stop();
        return Map.of("status", "stopped");
    }

    @GET
    @Path("/stats")
    public Map<String, Object> getStats() {
        Map<String, Long> basicStats = runner.getStats();

        // Map을 새로 만들어서 비율 계산 추가
        Map<String, Object> extendedStats = new HashMap<>(basicStats);

        long total = basicStats.getOrDefault("total", 0L);
        long success = basicStats.getOrDefault("success", 0L);
        long fail = basicStats.getOrDefault("fail", 0L);

        double successRate = (total == 0) ? 0.0 : (double) success / total * 100.0;
        double errorRate = (total == 0) ? 0.0 : (double) fail / total * 100.0;

        extendedStats.put("successRate", successRate);
        extendedStats.put("errorRate", errorRate);
        extendedStats.put("isRunning", runner.isRunning());

        return extendedStats;
    }

    @GET
    @Path("/stats/detailed")
    public com.gload.model.TestStatistics getDetailedStats() {
        return runner.getDetailedStats();
    }
}