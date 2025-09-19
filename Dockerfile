# --- Stage 1: build (есть mvn внутри образа, mvnw не нужен) ---
FROM maven:3.9.8-eclipse-temurin-21 AS build
WORKDIR /app

# копим всё, кроме того, что в .dockerignore
COPY . .

# Собираем без тестов (можно убрать -DskipTests, если нужны тесты)
RUN mvn -U clean package -DskipTests

# --- Stage 2: runtime (минимальный JRE) ---
FROM eclipse-temurin:21-jre
WORKDIR /app

# Копируем собранный JAR
# Если у вас другой артефакт — поправьте маску файла
COPY --from=build /app/target/*-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
