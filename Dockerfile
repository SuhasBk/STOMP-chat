FROM adoptopenjdk/maven-openjdk11 AS build
COPY src /home/websocks/src
COPY pom.xml /home/websocks
RUN mvn -f /home/websocks/pom.xml package spring-boot:repackage

FROM adoptopenjdk/openjdk11:alpine
COPY --from=build /home/websocks/target/*.jar ./stompchat.jar
EXPOSE 8080:8080
ENTRYPOINT [ "java", "-jar", "./stompchat.jar" ]