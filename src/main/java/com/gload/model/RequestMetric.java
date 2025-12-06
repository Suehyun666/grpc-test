package com.gload.model;

/**
 * 개별 요청의 상세 메트릭
 */
public class RequestMetric {
    private long requestId;
    private long timestamp;
    private long latencyMs;
    private boolean success;
    private String errorType;
    private String errorMessage;
    private String serviceName;
    private String methodName;

    public RequestMetric(long requestId, long timestamp, long latencyMs, boolean success) {
        this.requestId = requestId;
        this.timestamp = timestamp;
        this.latencyMs = latencyMs;
        this.success = success;
    }

    // Getters and Setters
    public long getRequestId() { return requestId; }
    public void setRequestId(long requestId) { this.requestId = requestId; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public long getLatencyMs() { return latencyMs; }
    public void setLatencyMs(long latencyMs) { this.latencyMs = latencyMs; }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getErrorType() { return errorType; }
    public void setErrorType(String errorType) { this.errorType = errorType; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    public String getMethodName() { return methodName; }
    public void setMethodName(String methodName) { this.methodName = methodName; }
}
