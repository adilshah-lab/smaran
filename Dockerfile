FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY . .

RUN chmod +x gradlew

# ✅ SKIP TESTS
RUN ./gradlew build -x test

ENTRYPOINT ["sh", "-c", "java -jar build/libs/*.jar"]