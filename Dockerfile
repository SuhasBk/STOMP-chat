FROM adoptopenjdk/openjdk11

COPY target/*.jar stompchat.jar
EXPOSE 8080:8080
ENTRYPOINT [ "java", "-jar", "/stompchat.jar" ]