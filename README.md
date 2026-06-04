# Incident Reporting System

An AST (Automated Software Testing) project built with TDD, Maven, Docker, and JPA/Hibernate.

## Tech Stack

- Java 8
- JUnit 4 + AssertJ
- Hibernate 5 / JPA (PostgreSQL)
- Testcontainers (integration tests with Docker)
- Maven
- Docker

## Build & Run

Requirements: Java 8, Maven, Docker

```bash
# Unit tests only (no Docker needed — uses H2 in-memory)
mvn clean test

# Unit + integration tests (Docker starts automatically via Testcontainers)
mvn clean verify
```

## Project Structure

```
src/main/java/incident_reporting/   application code
src/test/java/incident_reporting/   unit and integration tests
src/main/resources/META-INF/        JPA persistence config (PostgreSQL)
src/test/resources/META-INF/        JPA persistence config (H2 for unit tests)
docker-compose.yml                  local PostgreSQL for manual development
```

## Entities

- **User** — reports incidents (firstName, lastName, email, password)
- **Incident** — reported event with title, description, severity, timestamp, isClosed
- **Tag** — many-to-many with Incident
