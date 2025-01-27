## Como criar Dockerfile:

- Criar arquivo "Dockerfile"
  - Gera .jar da aplicação usando o mavem:
    ``mvn package spring-boot:repackage``
    - Cria o arquivo .jar na pasta target confirme nome do projeto, exemplo:
      - proposta_app-0.0.1-SNAPSHOT.jar
  - Usa os comandos abaixo dentro do dockerfile:
    ```json
      
    FROM openjdk:17
      
    COPY target/proposta_app-0.0.1-SNAPSHOT.jar proposta_app-0.0.1-SNAPSHOT.jar
      
    ENTRYPOINT ["java", "-jar", "proposta_app-0.0.1-SNAPSHOT.jar"]
     ```
  - Na pasta onde está o Dockerfile, executar do comando de build:
    `` docker build -t proposta_app .``