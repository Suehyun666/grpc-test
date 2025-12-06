package com.gload.core.grpc;

import com.github.os72.protocjar.Protoc;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class DynamicProtoCompiler {

    public static File compileProtoToDesc(Path protoFile) throws IOException, InterruptedException {
        // 임시 desc 파일 경로 생성
        Path descPath = Files.createTempFile("temp_", ".desc");

        // grpc 파일의 부모 디렉토리 결정
        Path protoPath = protoFile.getParent();
        if (protoPath == null) {
            protoPath = Path.of("."); // 현재 디렉토리 사용
        }

        // protoc 명령어 실행: protoc --descriptor_set_out=temp.desc --include_imports my.grpc
        List<String> args = List.of(
                "-v3.11.4", // protoc 버전
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
}