# PJ_springBoot_RabbitMQ
Projeto de microserviços com Spring Boot RabbitMQ e AWS

### Dependências Spring
- Spring Data JPA   //SQL
- Spring Web //WEB
- PostgreeSQL Driver //SQL
- Lombok //Developer Tools
- WebSocket //Messaging
- Spring for RabbitMQ //Messaging

### Imagens docker

#### FrontEnd

``docker run -d -p 80:80 --name proposta-web-container matheuspieropan/proposta-web``

#### PostgreSQL (docker-compose)

#### Definindo env:

```yaml

version: '3'
services:
  ## PostgreSQL
  postgreSQL:
    image: postgres
    restart: always
    environment:
      POSTGRES_DB: "propostadb"
      POSTGRES_USER: "postgres"
      POSTGRES_PASSWORD: "123"
    ports:
      # <Port exposed> : <MySQL Port running inside container>
      - "15432:5432"
    volumes:
      - ./data/postgres:/var/lib/postgresql/data
    networks:
      - postgres-network
  ## PGAdmin 4
  pgadmin:
    image: dpage/pgadmin4
    environment:
      PGADMIN_DEFAULT_EMAIL: "alissoncavalcanticma@gmail.com"
      PGADMIN_DEFAULT_PASSWORD: "321"
    ports:
      # <Port exposed> : <MySQL Port running inside container>
      - "8081:80"
    depends_on:
      - postgreSQL
    networks:
      - postgres-network
## Network
networks: 
  postgres-network:
    driver: bridge
```
#### Levantando env com docker compose:

`` docker-compose -f postgreSQL_env.yml up -d``