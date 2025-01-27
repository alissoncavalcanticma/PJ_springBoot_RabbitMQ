FROM openjdk:17

COPY target/proposta_app-0.0.1-SNAPSHOT.jar proposta_app-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java", "-jar", "proposta_app-0.0.1-SNAPSHOT.jar"]