FROM openjdk:17-jdk-slim
WORKDIR /app
COPY social/build/libs/app.jar app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]