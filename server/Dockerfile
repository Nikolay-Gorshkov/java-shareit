# --- build stage ---
FROM maven:3.9.8-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml ./pom.xml
COPY server/pom.xml server/pom.xml
COPY gateway/pom.xml gateway/pom.xml
COPY server ./server
COPY gateway ./gateway

ARG MVN_PROFILES=
RUN mvn -U -pl server -am -DskipTests -P ${MVN_PROFILES} package

# --- runtime stage ---
FROM eclipse-temurin:21-jre
WORKDIR /app

# фиксируем тайзону и опции JVM
ENV TZ=UTC
ENV JAVA_TOOL_OPTIONS="-Duser.timezone=UTC -Dfile.encoding=UTF-8"

COPY --from=build /app/server/target/*server*-SNAPSHOT.jar app.jar
EXPOSE 9090
ENTRYPOINT ["java","-jar","/app/app.jar"]
