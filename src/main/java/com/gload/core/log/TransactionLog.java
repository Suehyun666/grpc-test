package com.gload.core.log;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionLog {
    private long timestamp;
    private String userId;
    private String reqId;
    private String status;
    private long latency;
    private String errorMsg;
    private String responseBody;

    public static TransactionLog success(long reqId, long latency, String responseBody) {
        TransactionLog log = new TransactionLog();
        log.timestamp = System.currentTimeMillis();
        log.reqId = String.valueOf(reqId);
        log.status = "OK";
        log.latency = latency;
        if (responseBody != null && responseBody.length() > 1000) {
            log.responseBody = responseBody.substring(0, 1000) + "...(truncated)";
        } else {
            log.responseBody = responseBody;
        }
        return log;
    }

    public static TransactionLog error(long reqId, long latency, String errorMsg) {
        TransactionLog log = new TransactionLog();
        log.timestamp = System.currentTimeMillis();
        log.reqId = String.valueOf(reqId);
        log.status = "KO";
        log.latency = latency;
        log.errorMsg = errorMsg;
        return log;
    }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getReqId() { return reqId; }
    public void setReqId(String reqId) { this.reqId = reqId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public long getLatency() { return latency; }
    public void setLatency(long latency) { this.latency = latency; }

    public String getErrorMsg() { return errorMsg; }
    public void setErrorMsg(String errorMsg) { this.errorMsg = errorMsg; }

    public String getResponseBody() { return responseBody; }
    public void setResponseBody(String responseBody) { this.responseBody = responseBody; }
}
