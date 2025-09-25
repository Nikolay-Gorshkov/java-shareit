# --- build stage ---
FROM maven:3.9.8-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml ./pom.xml
COPY server/pom.xml server/pom.xml
COPY gateway/pom.xml gateway/pom.xml
COPY server ./server
COPY gateway ./gateway

# ВАЖНО: добавили -P h2, чтобы драйвер H2 вошёл в fat-jar
RUN mvn -U -pl server -am -DskipTests -P h2 package

# --- runtime stage ---
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/server/target/*server*-SNAPSHOT.jar app.jar
EXPOSE 9090
ENTRYPOINT ["java","-jar","/app/app.jar"]
