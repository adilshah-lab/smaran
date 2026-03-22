FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY . .

RUN ./gradlew build

ENTRYPOINT ["sh", "-c", "java -jar build/libs/*.jar"]