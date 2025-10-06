
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY target/bankx-api-gateway-1.0-SNAPSHOT.jar app.jar

# Пробрасываем порт, который слушает Gateway
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]