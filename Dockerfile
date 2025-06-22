FROM openjdk:21-jdk-slim

WORKDIR /app
RUN mkdir -p logs
COPY target/transfer-0.0.1.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
