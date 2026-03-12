FROM maven:3.8-openjdk-17 AS builder
WORKDIR /build
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn package -DskipTests -B

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=builder /build/target/hr-admin-1.0.0.jar app.jar
EXPOSE 8080
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar app.jar"]
