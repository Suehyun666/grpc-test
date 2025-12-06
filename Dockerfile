####
# 멀티스테이지 빌드를 사용한 Quarkus 애플리케이션 Docker 이미지
####

## 1단계: 빌드 스테이지
FROM gradle:8.5-jdk21 AS build
WORKDIR /app

# Gradle 래퍼 및 설정 파일 복사
COPY gradle gradle
COPY gradlew .
COPY gradlew.bat .
COPY gradle.properties .
COPY settings.gradle .
COPY build.gradle .

# 의존성 다운로드 (캐시 레이어)
RUN ./gradlew --no-daemon dependencies

# 소스 코드 복사
COPY src src

# 애플리케이션 빌드
RUN ./gradlew --no-daemon build -x test

## 2단계: 런타임 스테이지
FROM eclipse-temurin:21-jre
WORKDIR /app

# 빌드된 jar 파일 복사
COPY --from=build /app/build/quarkus-app/lib/ /app/lib/
COPY --from=build /app/build/quarkus-app/*.jar /app/
COPY --from=build /app/build/quarkus-app/app/ /app/app/
COPY --from=build /app/build/quarkus-app/quarkus/ /app/quarkus/

# 포트 노출 (Quarkus 기본 포트)
EXPOSE 52274

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "/app/quarkus-run.jar"]
