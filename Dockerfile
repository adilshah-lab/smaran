FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY . .

RUN chmod +x gradlew

RUN ./gradlew build -x test

ENTRYPOINT ["java", "-jar", "build/libs/prayer-lock-backend-0.0.1-SNAPSHOT.jar"]