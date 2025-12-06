package com.gload.web;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.*;

@ApplicationScoped
@Path("/api/config")
@Produces(MediaType.APPLICATION_JSON)
public class ConfigController {

    @GET
    public ServerConfig getConfig() {
        ServerConfig config = new ServerConfig();

        config.setSupportedModes(Arrays.asList(
            new ModeInfo("SINGLE", "Single Request", "Execute one request and return response"),
            new ModeInfo("CONSTANT_THROUGHPUT", "Constant Throughput", "Maintain constant RPS"),
            new ModeInfo("RAMP_UP", "Ramp Up", "Gradually increase load"),
            new ModeInfo("BURST", "Burst", "Sudden spike load")
        ));

        config.setSupportedFieldTypes(Arrays.asList(
            new FieldTypeInfo("FIXED", "Fixed Value", Arrays.asList("value"), "Use a static value"),
            new FieldTypeInfo("RANDOM_INT", "Random Integer", Arrays.asList("minValue", "maxValue"), "Generate random integer in range"),
            new FieldTypeInfo("UUID", "UUID", Collections.emptyList(), "Generate random UUID v4"),
            new FieldTypeInfo("SEQUENCE", "Sequence", Arrays.asList("minValue", "maxValue"), "Increment from min to max"),
            new FieldTypeInfo("RANDOM_STRING", "Random String", Collections.emptyList(), "Generate random string"),
            new FieldTypeInfo("CSV_FEEDER", "CSV Feeder", Arrays.asList("csvFilePath"), "Read values from CSV file")
        ));

        config.setFieldValueTypes(Arrays.asList("STRING", "INT32", "INT64", "DOUBLE", "FLOAT", "BOOL", "BYTES"));

        return config;
    }

    public static class ServerConfig {
        private List<ModeInfo> supportedModes;
        private List<FieldTypeInfo> supportedFieldTypes;
        private List<String> fieldValueTypes;

        public List<ModeInfo> getSupportedModes() { return supportedModes; }
        public void setSupportedModes(List<ModeInfo> supportedModes) { this.supportedModes = supportedModes; }

        public List<FieldTypeInfo> getSupportedFieldTypes() { return supportedFieldTypes; }
        public void setSupportedFieldTypes(List<FieldTypeInfo> supportedFieldTypes) { this.supportedFieldTypes = supportedFieldTypes; }

        public List<String> getFieldValueTypes() { return fieldValueTypes; }
        public void setFieldValueTypes(List<String> fieldValueTypes) { this.fieldValueTypes = fieldValueTypes; }
    }

    public static class ModeInfo {
        private String value;
        private String label;
        private String description;

        public ModeInfo() {}
        public ModeInfo(String value, String label, String description) {
            this.value = value;
            this.label = label;
            this.description = description;
        }

        public String getValue() { return value; }
        public void setValue(String value) { this.value = value; }

        public String getLabel() { return label; }
        public void setLabel(String label) { this.label = label; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    public static class FieldTypeInfo {
        private String type;
        private String label;
        private List<String> requiredParams;
        private String description;

        public FieldTypeInfo() {}
        public FieldTypeInfo(String type, String label, List<String> requiredParams, String description) {
            this.type = type;
            this.label = label;
            this.requiredParams = requiredParams;
            this.description = description;
        }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public String getLabel() { return label; }
        public void setLabel(String label) { this.label = label; }

        public List<String> getRequiredParams() { return requiredParams; }
        public void setRequiredParams(List<String> requiredParams) { this.requiredParams = requiredParams; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
}
