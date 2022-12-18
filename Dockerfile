FROM maven:3.8.3-amazoncorretto-17 AS build
COPY src /home/websocks/src
COPY pom.xml /home/websocks
RUN mvn -f /home/websocks/pom.xml package spring-boot:repackage

FROM amazoncorretto:17
COPY --from=build /home/websocks/target/*.jar ./stompchat.jar
EXPOSE 8000:8000
ENTRYPOINT [ "java", "-jar", "./stompchat.jar" ]