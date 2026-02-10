# Estado Atual e Decisões de Design

## Estado atual

- Stack:
  - Spring Boot 4
  - Java 21
  - PostgreSQL
  - Liquibase
  - RabbitMQ

- Contexto principal implementado: `customer`.

- Fluxo de criação de customer:
  1. API recebe `email` e `githubUsername`.
  2. Customer é salvo rapidamente no banco.
  3. Evento `customer.created` é publicado no RabbitMQ.
  4. Consumer processa o evento.
  5. Consumer consulta GitHub API.
  6. Nome do customer é enriquecido e persistido.

- Observabilidade:
  - `X-Correlation-Id` por request.
  - `MDC` para propagar correlação nos logs.
  - Logs estruturados no controller, use case, publisher, consumer e cliente GitHub.

## Decisões de design

- Organização por módulo (`customer`) com separação por responsabilidade:
  - `domain`: entidade e exceções de negócio.
  - `usecases`: regras de aplicação/orquestração.
  - `infrastructure`: integração externa (DB, Rabbit, GitHub).
  - `presentation`: REST controller, DTOs e handler HTTP.

- Mantido `UseCase` como unidade de regra de negócio.

- Removida camada `port/adapter` para reduzir complexidade neste momento.
  - Use cases dependem diretamente de `CustomerJpaRepository` e `GitHubClient`.

- Criação de customer assíncrona para desacoplar API da latência/instabilidade do GitHub.

- Migração de schema com Liquibase e `ddl-auto: validate` para evitar drift de banco.

## Convenções atuais

- Package raiz: `main`.
- Correlation header: `X-Correlation-Id`.
- Evento de criação: exchange `customer.exchange`, routing key `customer.created`, queue `customer.created.queue`.
