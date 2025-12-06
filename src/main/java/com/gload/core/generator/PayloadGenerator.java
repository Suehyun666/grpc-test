package com.gload.core.generator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Map;

/**
 * 필드별 Generator를 조합하여 하나의 JSON 요청 본문을 만드는 클래스
 * 예: { "name": "RandomString", "age": 25 }
 */
public class PayloadGenerator {
    private final Map<String, ValueGenerator> fieldGenerators;
    private final ObjectMapper mapper = new ObjectMapper();

    public PayloadGenerator(Map<String, ValueGenerator> fieldGenerators) {
        this.fieldGenerators = fieldGenerators;
    }

    public String generateJson() {
        ObjectNode root = mapper.createObjectNode();

        fieldGenerators.forEach((fieldName, generator) -> {
            Object value = generator.nextValue();

            if (value instanceof Integer) {
                root.put(fieldName, (Integer) value);
            } else if (value instanceof Long) {
                root.put(fieldName, (Long) value);
            } else if (value instanceof Boolean) {
                root.put(fieldName, (Boolean) value);
            } else {
                root.put(fieldName, String.valueOf(value));
            }
        });

        return root.toString();
    }
}