# gRPC Load Tester - Frontend

Gatling 스타일의 gRPC 부하 테스트 도구 프론트엔드

## 주요 기능

### 1. Simulation 관리
- 테스트 시나리오 저장/불러오기/삭제
- Gatling처럼 Simulation을 파일로 관리

### 2. 부하 테스트
- **Load Modes**: SINGLE, CONSTANT_THROUGHPUT, RAMP_UP, BURST
- **Virtual Users**: 동시 접속 클라이언트 수 설정
- **Target RPS**: 초당 요청 수 제어
- **Worker Threads**: Netty EventLoop 스레드 수

### 3. Payload 필드 규칙 설정
각 필드별로 데이터 생성 규칙 설정:
- **FIXED**: 고정값
- **RANDOM_INT**: 랜덤 정수 범위 (예: 1001~2000)
- **RANDOM_STRING**: 랜덤 문자열
- **UUID**: UUID 자동 생성
- **SEQUENCE**: 순차 증가 (예: 1~1000 순환)

### 4. 실시간 통계
- **Latency 분포**: P50, P95, P99, Min, Max, Avg
- **Success/Fail Rate**: 성공/실패 비율
- **Error Analysis**: 에러 타입별 분류
- **Request Tracking**: 요청 번호별 Latency

## 개발 워크플로우

### 개발 모드
```bash
# 백엔드 (8080)
cd ../grpc-test && ./gradlew quarkusDev

# 프론트엔드 (5173, proxy → 8080)
npm install
npm run dev
```

### 프로덕션 빌드
```bash
npm run build
cd ../grpc-test && ./gradlew build
```

## Simulation 저장 위치
`grpc-test/simulations/*.json`
