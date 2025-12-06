package com.gload.core.storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gload.model.Simulation;
import jakarta.enterprise.context.ApplicationScoped;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Simulation 파일 저장소
 * simulations/ 디렉토리에 JSON 형태로 저장
 */
@ApplicationScoped
public class SimulationStorage {

    private static final String STORAGE_DIR = "simulations";
    private final ObjectMapper mapper;

    public SimulationStorage() {
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());

        // 디렉토리 생성
        File dir = new File(STORAGE_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    /**
     * Simulation 저장
     */
    public void save(Simulation simulation) throws IOException {
        if (simulation.getId() == null || simulation.getId().isEmpty()) {
            simulation.setId(UUID.randomUUID().toString());
        }

        String filename = simulation.getId() + ".json";
        Path filePath = Paths.get(STORAGE_DIR, filename);
        mapper.writerWithDefaultPrettyPrinter().writeValue(filePath.toFile(), simulation);
    }

    /**
     * Simulation 로드
     */
    public Simulation load(String id) throws IOException {
        String filename = id + ".json";
        Path filePath = Paths.get(STORAGE_DIR, filename);

        if (!Files.exists(filePath)) {
            return null;
        }

        return mapper.readValue(filePath.toFile(), Simulation.class);
    }

    /**
     * 모든 Simulation 목록 조회
     */
    public List<Simulation> listAll() throws IOException {
        File dir = new File(STORAGE_DIR);
        File[] files = dir.listFiles((d, name) -> name.endsWith(".json"));

        if (files == null) {
            return Collections.emptyList();
        }

        List<Simulation> simulations = new ArrayList<>();
        for (File file : files) {
            try {
                Simulation sim = mapper.readValue(file, Simulation.class);
                simulations.add(sim);
            } catch (Exception e) {
                System.err.println("Failed to load simulation: " + file.getName());
            }
        }

        // 최근 생성 순으로 정렬
        simulations.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
        return simulations;
    }

    /**
     * Simulation 삭제
     */
    public boolean delete(String id) {
        String filename = id + ".json";
        Path filePath = Paths.get(STORAGE_DIR, filename);

        try {
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            System.err.println("Failed to delete simulation: " + id);
            return false;
        }
    }
}
