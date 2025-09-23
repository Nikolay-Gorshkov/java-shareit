# --- build stage ---
FROM maven:3.9.8-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml ./pom.xml
COPY server/pom.xml server/pom.xml
COPY gateway/pom.xml gateway/pom.xml

COPY server ./server
COPY gateway ./gateway

RUN mvn -U -pl gateway -am -DskipTests package

# --- runtime stage ---
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/gateway/target/*gateway*-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
