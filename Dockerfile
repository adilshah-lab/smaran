FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY . .

# ✅ ADD THIS LINE
RUN chmod +x gradlew

RUN ./gradlew build

ENTRYPOINT ["sh", "-c", "java -jar build/libs/*.jar"]