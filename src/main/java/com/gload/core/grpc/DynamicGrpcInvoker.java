package com.gload.core.grpc;

import com.google.protobuf.Descriptors;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.util.JsonFormat;
import io.grpc.*;
import io.grpc.protobuf.ProtoUtils;
import io.grpc.stub.ClientCalls;
import io.grpc.stub.MetadataUtils;
import io.grpc.stub.StreamObserver;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 동적 gRPC 호출기
 * Proto 파일을 컴파일하지 않고도 런타임에 gRPC 요청을 보낼 수 있게 해주는 핵심 클래스
 *
 * 작동 원리:
 * 1. Protobuf Descriptor에서 메서드 정보 추출
 * 2. JSON -> DynamicMessage 변환
 * 3. gRPC ClientCalls로 비동기 호출
 */
public class DynamicGrpcInvoker {

    private final ManagedChannel baseChannel;

    public DynamicGrpcInvoker(ManagedChannel channel) {
        this.baseChannel = channel;
    }

    /**
     * 비동기 Unary 호출
     *
     * @param methodDesc Proto에서 추출한 메서드 정보
     * @param jsonPayload 요청 데이터 (JSON 형식)
     * @param observer 응답 콜백
     */
    public void callAsync(
            Descriptors.MethodDescriptor methodDesc,
            String jsonPayload,
            StreamObserver<DynamicMessage> observer) {

        try {
            // 1. gRPC MethodDescriptor 생성
            MethodDescriptor<DynamicMessage, DynamicMessage> grpcMethodDescriptor =
                createMethodDescriptor(methodDesc);

            // 2. JSON을 DynamicMessage로 변환
            DynamicMessage requestMessage = parseJsonToMessage(methodDesc.getInputType(), jsonPayload);

            // 3. 비동기 호출
            ClientCalls.asyncUnaryCall(
                baseChannel.newCall(grpcMethodDescriptor, CallOptions.DEFAULT),
                requestMessage,
                observer
            );

        } catch (Exception e) {
            observer.onError(e);
        }
    }

    /**
     * DynamicMessage를 직접 받아서 호출하는 버전
     * (ValueGenerator로 생성한 메시지를 바로 보낼 때 사용)
     */
    public void callAsync(
            Descriptors.MethodDescriptor methodDesc,
            String jsonPayload,
            long timeoutSec, // <--- 추가됨
            StreamObserver<DynamicMessage> observer) {

        try {
            MethodDescriptor<DynamicMessage, DynamicMessage> grpcMethodDescriptor =
                    createMethodDescriptor(methodDesc);

            DynamicMessage requestMessage = parseJsonToMessage(methodDesc.getInputType(), jsonPayload);

            // [추가] Timeout(Deadline) 설정
            CallOptions callOptions = CallOptions.DEFAULT;
            if (timeoutSec > 0) {
                callOptions = callOptions.withDeadlineAfter(timeoutSec, TimeUnit.SECONDS);
            }

            ClientCalls.asyncUnaryCall(
                    baseChannel.newCall(grpcMethodDescriptor, callOptions),
                    requestMessage,
                    observer
            );

        } catch (Exception e) {
            observer.onError(e);
        }
    }

    /**
     * Metadata를 포함한 비동기 호출
     */
    public void callAsync(
            Descriptors.MethodDescriptor methodDesc,
            String jsonPayload,
            long timeoutSec,
            Map<String, String> metadata,
            StreamObserver<DynamicMessage> observer) {

        try {
            MethodDescriptor<DynamicMessage, DynamicMessage> grpcMethodDescriptor =
                    createMethodDescriptor(methodDesc);

            DynamicMessage requestMessage = parseJsonToMessage(methodDesc.getInputType(), jsonPayload);

            // Timeout(Deadline) 설정
            CallOptions callOptions = CallOptions.DEFAULT;
            if (timeoutSec > 0) {
                callOptions = callOptions.withDeadlineAfter(timeoutSec, TimeUnit.SECONDS);
            }

            // Metadata가 있으면 Interceptor를 통해 Channel을 래핑
            Channel channelToUse = baseChannel;
            if (metadata != null && !metadata.isEmpty()) {
                Metadata grpcMetadata = new Metadata();
                for (Map.Entry<String, String> entry : metadata.entrySet()) {
                    Metadata.Key<String> key = Metadata.Key.of(entry.getKey(), Metadata.ASCII_STRING_MARSHALLER);
                    grpcMetadata.put(key, entry.getValue());
                }
                ClientInterceptor interceptor = MetadataUtils.newAttachHeadersInterceptor(grpcMetadata);
                channelToUse = ClientInterceptors.intercept(baseChannel, interceptor);
            }

            ClientCalls.asyncUnaryCall(
                    channelToUse.newCall(grpcMethodDescriptor, callOptions),
                    requestMessage,
                    observer
            );

        } catch (Exception e) {
            observer.onError(e);
        }
    }

    /**
     * gRPC MethodDescriptor 생성
     * DynamicMessage를 직렬화/역직렬화할 수 있는 Marshaller 포함
     */
    private MethodDescriptor<DynamicMessage, DynamicMessage> createMethodDescriptor(
            Descriptors.MethodDescriptor methodDesc) {

        String fullMethodName = MethodDescriptor.generateFullMethodName(
            methodDesc.getService().getFullName(),
            methodDesc.getName()
        );

        DynamicMessage requestDefaultInstance =
            DynamicMessage.getDefaultInstance(methodDesc.getInputType());
        DynamicMessage responseDefaultInstance =
            DynamicMessage.getDefaultInstance(methodDesc.getOutputType());

        return MethodDescriptor.<DynamicMessage, DynamicMessage>newBuilder()
            .setType(MethodDescriptor.MethodType.UNARY)
            .setFullMethodName(fullMethodName)
            .setRequestMarshaller(ProtoUtils.marshaller(requestDefaultInstance))
            .setResponseMarshaller(ProtoUtils.marshaller(responseDefaultInstance))
            .build();
    }

    /**
     * JSON 문자열을 DynamicMessage로 변환
     * JsonFormat을 사용하면 자동으로 필드 검증도 수행됨
     */
    private DynamicMessage parseJsonToMessage(
            Descriptors.Descriptor messageType,
            String jsonPayload) throws Exception {

        DynamicMessage.Builder builder = DynamicMessage.newBuilder(messageType);
        JsonFormat.parser()
            .ignoringUnknownFields()
            .merge(jsonPayload, builder);

        return builder.build();
    }

    /**
     * DynamicMessage를 JSON으로 변환 (응답 로깅용)
     */
    public static String messageToJson(DynamicMessage message) throws Exception {
        return JsonFormat.printer()
            .includingDefaultValueFields()
            .print(message);
    }
}
