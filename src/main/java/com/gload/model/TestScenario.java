package com.gload.model;

import java.util.HashMap;
import java.util.List; // List import 추가
import java.util.Map;

/**
 * gRPC 테스트 시나리오
 * Proto 파일, 타겟 서비스, 부하 조건, 데이터 생성 규칙을 모두 담는 컨테이너
 */
public class TestScenario {

    private String name;                    // 시나리오 이름
    private String protoFilePath;           // .grpc 파일 경로 또는 .desc 파일 경로
    private String serviceName;             // 예: "my.package.OrderService"
    private String methodName;              // 예: "CreateOrder"
    private String endpoint;                // 예: "localhost:50051"
    private boolean useTls;                 // TLS 사용 여부
    private String tlsCertPath;             // TLS 인증서 경로
    private int timeoutSec;                 // gRPC 요청 타임아웃 (초)

    private LoadProfile loadProfile;        // 부하 프로필
    private Map<String, FieldRule> fieldRules; // 필드별 데이터 생성 규칙
    private Map<String, String> metadata;   // gRPC 메타데이터 (헤더)

    public TestScenario() {
        this.loadProfile = new LoadProfile();
        this.fieldRules = new HashMap<>();
        this.metadata = new HashMap<>();
        this.useTls = false;
        this.timeoutSec = 10; // 기본값 10초
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getProtoFilePath() { return protoFilePath; }
    public void setProtoFilePath(String protoFilePath) { this.protoFilePath = protoFilePath; }

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    public String getMethodName() { return methodName; }
    public void setMethodName(String methodName) { this.methodName = methodName; }

    public String getEndpoint() { return endpoint; }
    public void setEndpoint(String endpoint) { this.endpoint = endpoint; }

    public boolean isUseTls() { return useTls; }
    public void setUseTls(boolean useTls) { this.useTls = useTls; }

    public String getTlsCertPath() { return tlsCertPath; }
    public void setTlsCertPath(String tlsCertPath) { this.tlsCertPath = tlsCertPath; }

    public int getTimeoutSec() { return timeoutSec; }
    public void setTimeoutSec(int timeoutSec) { this.timeoutSec = timeoutSec; }

    public LoadProfile getLoadProfile() { return loadProfile; }
    public void setLoadProfile(LoadProfile loadProfile) { this.loadProfile = loadProfile; }

    public Map<String, FieldRule> getFieldRules() { return fieldRules; }
    public void setFieldRules(Map<String, FieldRule> fieldRules) { this.fieldRules = fieldRules; }

    public Map<String, String> getMetadata() { return metadata; }
    public void setMetadata(Map<String, String> metadata) { this.metadata = metadata; }

    /**
     * 필드별 데이터 생성 규칙
     */
    public static class FieldRule {
        public enum Type {
            FIXED,          // 고정값
            RANDOM_INT,     // 랜덤 정수 (범위 지정)
            RANDOM_STRING,  // 랜덤 문자열
            UUID,           // UUID 생성
            SEQUENCE,       // 순차 증가
            CSV_FEEDER,     // CSV 파일에서 읽기
            ROUND_ROBIN     // Round Robin (리스트에서 순환)
        }

        private Type type;
        private Object value;           // FIXED 모드용
        private Integer minValue;       // RANDOM_INT, SEQUENCE 모드용
        private Integer maxValue;       // RANDOM_INT, SEQUENCE 모드용
        private String csvFilePath;     // CSV_FEEDER 모드용 (파일 경로)
        private List<String> csvValues; // CSV_FEEDER 모드용 (직접 데이터 주입)

        // Getters and Setters
        public Type getType() { return type; }
        public void setType(Type type) { this.type = type; }

        public Object getValue() { return value; }
        public void setValue(Object value) { this.value = value; }

        public Integer getMinValue() { return minValue; }
        public void setMinValue(Integer minValue) { this.minValue = minValue; }

        public Integer getMaxValue() { return maxValue; }
        public void setMaxValue(Integer maxValue) { this.maxValue = maxValue; }

        public String getCsvFilePath() { return csvFilePath; }
        public void setCsvFilePath(String csvFilePath) { this.csvFilePath = csvFilePath; }

        public List<String> getCsvValues() { return csvValues; }
        public void setCsvValues(List<String> csvValues) { this.csvValues = csvValues; }
    }
}