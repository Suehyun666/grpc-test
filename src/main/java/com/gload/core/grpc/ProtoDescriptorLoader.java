package com.gload.core.grpc;

import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Proto 파일(.desc 형식)을 로드하고 Descriptor를 생성하는 클래스
 *
 * 사용법:
 * 1. protoc로 .desc 파일 생성: protoc --descriptor_set_out=order.desc order.grpc
 * 2. 이 클래스로 로드: ProtoDescriptorLoader.load("order.desc")
 */
public class ProtoDescriptorLoader {

    public static Descriptors.FileDescriptor load(String descFilePath) throws IOException, Descriptors.DescriptorValidationException {
        try (FileInputStream fis = new FileInputStream(descFilePath)) {
            DescriptorProtos.FileDescriptorSet descriptorSet =
                    DescriptorProtos.FileDescriptorSet.parseFrom(fis);

            Map<String, DescriptorProtos.FileDescriptorProto> protoMap = new HashMap<>();
            for (DescriptorProtos.FileDescriptorProto fdProto : descriptorSet.getFileList()) {
                protoMap.put(fdProto.getName(), fdProto);
            }

            Map<String, Descriptors.FileDescriptor> cache = new HashMap<>();

            DescriptorProtos.FileDescriptorProto mainProto = descriptorSet.getFile(descriptorSet.getFileCount() - 1);

            return buildFileDescriptor(mainProto.getName(), protoMap, cache);
        }
    }

    public static List<Descriptors.FileDescriptor> loadAll(String descFilePath) throws IOException, Descriptors.DescriptorValidationException {
        try (FileInputStream fis = new FileInputStream(descFilePath)) {
            DescriptorProtos.FileDescriptorSet descriptorSet =
                    DescriptorProtos.FileDescriptorSet.parseFrom(fis);

            Map<String, DescriptorProtos.FileDescriptorProto> protoMap = new HashMap<>();
            for (DescriptorProtos.FileDescriptorProto fdProto : descriptorSet.getFileList()) {
                protoMap.put(fdProto.getName(), fdProto);
            }

            Map<String, Descriptors.FileDescriptor> cache = new HashMap<>();
            List<Descriptors.FileDescriptor> allDescriptors = new ArrayList<>();

            for (DescriptorProtos.FileDescriptorProto fdProto : descriptorSet.getFileList()) {
                Descriptors.FileDescriptor fd = buildFileDescriptor(fdProto.getName(), protoMap, cache);
                allDescriptors.add(fd);
            }

            return allDescriptors;
        }
    }

    private static Descriptors.FileDescriptor buildFileDescriptor(
            String name,
            Map<String, DescriptorProtos.FileDescriptorProto> protoMap,
            Map<String, Descriptors.FileDescriptor> cache) throws Descriptors.DescriptorValidationException {

        // 이미 빌드했으면 반환
        if (cache.containsKey(name)) {
            return cache.get(name);
        }

        DescriptorProtos.FileDescriptorProto proto = protoMap.get(name);
        if (proto == null) {
            // standard google protobuf import (ex: google/protobuf/timestamp.grpc) 등은
            // 컴파일러 내장이라 없을 수 있음. 이 경우 처리가 복잡하므로 예외 처리 혹은 스킵 필요.
            throw new IllegalArgumentException("Dependency not found in descriptor set: " + name);
        }

        // 의존성(Imports) 먼저 재귀적으로 빌드
        List<Descriptors.FileDescriptor> dependencies = new ArrayList<>();
        for (String dependencyName : proto.getDependencyList()) {
            dependencies.add(buildFileDescriptor(dependencyName, protoMap, cache));
        }

        // 현재 파일 빌드 (의존성 주입)
        Descriptors.FileDescriptor fd = Descriptors.FileDescriptor.buildFrom(
                proto,
                dependencies.toArray(new Descriptors.FileDescriptor[0])
        );

        cache.put(name, fd);
        return fd;
    }

    public static Descriptors.MethodDescriptor findMethod(
            Descriptors.FileDescriptor fileDescriptor,
            String serviceName,
            String methodName) {

        Descriptors.ServiceDescriptor serviceDescriptor = fileDescriptor.findServiceByName(serviceName);
        if (serviceDescriptor == null) {
            throw new IllegalArgumentException("Service not found: " + serviceName);
        }

        Descriptors.MethodDescriptor methodDescriptor = serviceDescriptor.findMethodByName(methodName);
        if (methodDescriptor == null) {
            throw new IllegalArgumentException("Method not found: " + methodName);
        }

        return methodDescriptor;
    }

    public static Descriptors.MethodDescriptor findMethod(
            List<Descriptors.FileDescriptor> fileDescriptors,
            String serviceName,
            String methodName) {

        for (Descriptors.FileDescriptor fd : fileDescriptors) {
            Descriptors.ServiceDescriptor serviceDescriptor = fd.findServiceByName(serviceName);
            if (serviceDescriptor != null) {
                Descriptors.MethodDescriptor methodDescriptor = serviceDescriptor.findMethodByName(methodName);
                if (methodDescriptor == null) {
                    throw new IllegalArgumentException("Method not found: " + methodName);
                }
                return methodDescriptor;
            }
        }

        throw new IllegalArgumentException("Service not found: " + serviceName);
    }

    /**
     * FileDescriptor에서 모든 서비스와 메서드 정보 추출 (UI용)
     */
    public static Map<String, List<String>> extractServiceMethods(Descriptors.FileDescriptor fileDescriptor) {
        Map<String, List<String>> result = new HashMap<>();

        for (Descriptors.ServiceDescriptor service : fileDescriptor.getServices()) {
            List<String> methods = new ArrayList<>();
            for (Descriptors.MethodDescriptor method : service.getMethods()) {
                methods.add(method.getName());
            }
            result.put(service.getName(), methods);
        }

        return result;
    }

    /**
     * 메시지 타입의 필드 정보 추출 (UI용)
     */
    public static List<FieldInfo> extractFieldInfo(Descriptors.Descriptor messageDescriptor) {
        List<FieldInfo> fields = new ArrayList<>();

        for (Descriptors.FieldDescriptor field : messageDescriptor.getFields()) {
            FieldInfo info = new FieldInfo();
            info.setName(field.getName());
            info.setType(field.getType().name());
            info.setRepeated(field.isRepeated());
            info.setRequired(!field.isOptional());
            fields.add(info);
        }

        return fields;
    }

    /**
     * 필드 정보 DTO
     */
    public static class FieldInfo {
        private String name;
        private String type;
        private boolean repeated;
        private boolean required;

        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public boolean isRepeated() { return repeated; }
        public void setRepeated(boolean repeated) { this.repeated = repeated; }

        public boolean isRequired() { return required; }
        public void setRequired(boolean required) { this.required = required; }

        @Override
        public String toString() {
            return String.format("%s: %s%s%s",
                name,
                type,
                repeated ? "[]" : "",
                required ? " (required)" : "");
        }
    }
}
