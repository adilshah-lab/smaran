FROM openjdk:21-jdk-slim

WORKDIR /app

COPY . .

RUN ./gradlew build

ENTRYPOINT ["java", "-jar", "build/libs/*.jar"]