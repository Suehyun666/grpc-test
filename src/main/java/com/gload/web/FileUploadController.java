package com.gload.web;

import com.gload.core.grpc.DynamicProtoCompiler;
import com.gload.core.grpc.ProtoDescriptorLoader;
import com.google.protobuf.Descriptors;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Path("/api/upload")
@Produces(MediaType.APPLICATION_JSON)
public class FileUploadController {

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public ProtoInfo uploadProto(@RestForm("files") List<FileUpload> files) {
        try {
            if (files == null || files.isEmpty()) {
                throw new IllegalArgumentException("No files uploaded");
            }

            java.nio.file.Path tempDir = Files.createTempDirectory("gload_proto_");

            List<String> protoFilePaths = new ArrayList<>();

            for (FileUpload file : files) {
                String fileName = file.fileName();
                java.nio.file.Path filePath = tempDir.resolve(fileName);
                Files.copy(file.uploadedFile(), filePath, StandardCopyOption.REPLACE_EXISTING);

                if (fileName.toLowerCase().endsWith(".proto") || fileName.toLowerCase().endsWith(".grpc")) {
                    protoFilePaths.add(filePath.toAbsolutePath().toString());
                }
            }

            if (protoFilePaths.isEmpty()) {
                throw new IllegalArgumentException("No .proto or .grpc file found in upload");
            }

            // Compile all proto files together into one descriptor
            File descFile = DynamicProtoCompiler.compileProtoToDesc(
                tempDir,
                protoFilePaths.stream().map(p -> new File(p).toPath()).toList()
            );

            Descriptors.FileDescriptor fileDescriptor = ProtoDescriptorLoader.load(descFile.getAbsolutePath());

            List<ServiceInfo> services = new ArrayList<>();
            String firstProtoPath = protoFilePaths.get(0); // For response path field

            for (Descriptors.ServiceDescriptor serviceDesc : fileDescriptor.getServices()) {
                ServiceInfo serviceInfo = new ServiceInfo();
                serviceInfo.setName(serviceDesc.getName());

                List<MethodInfo> methods = new ArrayList<>();
                for (Descriptors.MethodDescriptor methodDesc : serviceDesc.getMethods()) {
                    MethodInfo methodInfo = new MethodInfo();
                    methodInfo.setName(methodDesc.getName());
                    methodInfo.setInputType(methodDesc.getInputType().getName());
                    methodInfo.setOutputType(methodDesc.getOutputType().getName());

                    List<FieldInfo> fields = new ArrayList<>();
                    for (Descriptors.FieldDescriptor fieldDesc : methodDesc.getInputType().getFields()) {
                        FieldInfo fieldInfo = new FieldInfo();
                        fieldInfo.setName(fieldDesc.getName());
                        fieldInfo.setType(fieldDesc.getType().name());
                        fieldInfo.setRepeated(fieldDesc.isRepeated());
                        fields.add(fieldInfo);
                    }
                    methodInfo.setFields(fields);

                    methods.add(methodInfo);
                }
                serviceInfo.setMethods(methods);
                services.add(serviceInfo);
            }

            ProtoInfo protoInfo = new ProtoInfo();
            protoInfo.setPath(firstProtoPath);
            protoInfo.setServices(services);

            return protoInfo;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("File upload failed: " + e.getMessage());
        }
    }

    // DTOs
    public static class ProtoInfo {
        private String path;
        private List<ServiceInfo> services;

        public String getPath() { return path; }
        public void setPath(String path) { this.path = path; }

        public List<ServiceInfo> getServices() { return services; }
        public void setServices(List<ServiceInfo> services) { this.services = services; }
    }

    public static class ServiceInfo {
        private String name;
        private List<MethodInfo> methods;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public List<MethodInfo> getMethods() { return methods; }
        public void setMethods(List<MethodInfo> methods) { this.methods = methods; }
    }

    public static class MethodInfo {
        private String name;
        private String inputType;
        private String outputType;
        private List<FieldInfo> fields;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getInputType() { return inputType; }
        public void setInputType(String inputType) { this.inputType = inputType; }

        public String getOutputType() { return outputType; }
        public void setOutputType(String outputType) { this.outputType = outputType; }

        public List<FieldInfo> getFields() { return fields; }
        public void setFields(List<FieldInfo> fields) { this.fields = fields; }
    }

    public static class FieldInfo {
        private String name;
        private String type;
        private boolean repeated;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public boolean isRepeated() { return repeated; }
        public void setRepeated(boolean repeated) { this.repeated = repeated; }
    }
}
