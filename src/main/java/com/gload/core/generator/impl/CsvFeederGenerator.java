package com.gload.core.generator.impl;

import com.gload.core.generator.ValueGenerator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class CsvFeederGenerator implements ValueGenerator {

    private final List<String> values;
    private final AtomicInteger index;
    private final boolean circular;

    /**
     * 생성자 1: 파일 경로로부터 로드
     */
    public CsvFeederGenerator(String csvFilePath) throws IOException {
        this.values = new ArrayList<>();
        this.index = new AtomicInteger(0);
        this.circular = true;

        try (BufferedReader reader = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    values.add(line);
                }
            }
        }

        if (values.isEmpty()) {
            throw new IllegalArgumentException("CSV file is empty: " + csvFilePath);
        }
    }

    /**
     * 생성자 2: 이미 로드된 리스트 사용
     */
    public CsvFeederGenerator(List<String> values) {
        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException("Values list cannot be empty");
        }
        this.values = new ArrayList<>(values);
        this.index = new AtomicInteger(0);
        this.circular = true;
    }

    @Override
    public Object nextValue() { // [Fix] 메서드명 수정 (nextvalue -> nextValue)
        // AtomicInteger는 계속 증가하므로 음수 처리를 위해 Math.abs나 나머지 연산 처리가 필요
        int currentIndex = index.getAndIncrement();

        if (currentIndex < 0) { // 오버플로우 방지
            index.set(0);
            currentIndex = 0;
        }

        if (circular) {
            return values.get(currentIndex % values.size());
        } else {
            if (currentIndex >= values.size()) {
                return values.get(values.size() - 1);
            }
            return values.get(currentIndex);
        }
    }
}