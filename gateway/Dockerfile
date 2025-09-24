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

# единая таймзона/encoding для корректной работы дат
ENV TZ=UTC
ENV JAVA_TOOL_OPTIONS="-Duser.timezone=UTC -Dfile.encoding=UTF-8"

# curl нужен для docker-compose healthcheck
RUN apt-get update && apt-get install -y --no-install-recommends curl && rm -rf /var/lib/apt/lists/*

COPY --from=build /app/gateway/target/*gateway*-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
