FROM maven:3.8.6-eclipse-temurin-11-alpine AS build
COPY src /home/websocks/src
COPY pom.xml /home/websocks
RUN mvn -f /home/websocks/pom.xml package spring-boot:repackage

FROM eclipse-temurin:11-alpine
COPY --from=build /home/websocks/target/*.jar ./stompchat.jar
EXPOSE 8000:8000
ENTRYPOINT [ "java", "-jar", "./stompchat.jar" ]