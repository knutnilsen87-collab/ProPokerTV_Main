# Backend Developer README

## Stack
- Java 21
- Spring Boot 3
- Spring Security
- Spring Data JPA
- PostgreSQL
- Flyway
- springdoc OpenAPI
- JJWT
- Testcontainers

## Project style
This backend uses **package-by-feature** and a **modular monolith** approach.
Each feature owns its:
- api/controller
- service/application
- dto
- domain/entity
- repository
- mapper/assembler where useful

## Modules
- auth
- user
- profile
- creator
- clip
- comment
- reaction
- moderation
- contest
- leaderboard
- storage
- common

## Local setup
1. Copy `.env.example` to `.env`
2. Start infrastructure:
   - `docker compose up -d`
   - PostgreSQL is published on host port `55436` to avoid collisions with other local Postgres instances
3. Run the application in your IDE or with Maven
4. App should start with Flyway migrations and seed data
5. Open:
   - API docs: `/swagger-ui/index.html`
   - Health: `/actuator/health`
   - OpenAPI: `/v3/api-docs`

## First recommended implementation order
1. Auth token service + refresh token persistence
2. Profile + creator profile E2E flow
3. Clip create/list/detail/update/delete
4. Comment + reaction flow
5. Moderation queue and reporting
6. Weekly contest + voting + leaderboard snapshot job

## Conventions
- Controllers return `ApiEnvelope<T>`
- Domain exceptions extend `DomainException`
- Validation annotations live on request DTOs
- Ownership/role checks happen in service layer and method security
- Database changes always go through Flyway migrations
- All externally visible timestamps use UTC
