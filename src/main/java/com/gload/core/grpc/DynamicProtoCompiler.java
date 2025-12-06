package com.gload.core.grpc;

import com.github.os72.protocjar.Protoc;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class DynamicProtoCompiler {

    public static File compileProtoToDesc(Path protoFile) throws IOException, InterruptedException {
        Path descPath = Files.createTempFile("temp_", ".desc");
        Path protoPath = protoFile.getParent();
        if (protoPath == null) {
            protoPath = Path.of(".");
        }

        List<String> args = List.of(
                "-v3.11.4",
                "--descriptor_set_out=" + descPath.toAbsolutePath().toString(),
                "--include_imports",
                "--proto_path=" + protoPath.toAbsolutePath().toString(),
                protoFile.toAbsolutePath().toString()
        );

        int exitCode = Protoc.runProtoc(args.toArray(new String[0]));
        if (exitCode != 0) {
            throw new RuntimeException("Protoc compilation failed");
        }

        return descPath.toFile();
    }

    public static File compileProtoToDesc(Path protoDir, List<Path> protoFiles) throws IOException, InterruptedException {
        if (protoFiles == null || protoFiles.isEmpty()) {
            throw new IllegalArgumentException("No proto files to compile");
        }

        Path descPath = Files.createTempFile("temp_", ".desc");

        List<String> args = new ArrayList<>();
        args.add("-v3.11.4");
        args.add("--descriptor_set_out=" + descPath.toAbsolutePath().toString());
        args.add("--include_imports");
        args.add("--proto_path=" + protoDir.toAbsolutePath().toString());

        for (Path protoFile : protoFiles) {
            args.add(protoFile.toAbsolutePath().toString());
        }

        int exitCode = Protoc.runProtoc(args.toArray(new String[0]));
        if (exitCode != 0) {
            throw new RuntimeException("Protoc compilation failed");
        }

        return descPath.toFile();
    }
}