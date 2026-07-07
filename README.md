# Incident Reporting System

Incident reporting system built with Java, PostgreSQL, Swing UI, and tested with TDD.

[![Build Status](https://github.com/msohailse/incident-reporting-system/actions/workflows/maven.yml/badge.svg)](https://github.com/msohailse/incident-reporting-system/actions)
[![Coverage Status](https://coveralls.io/repos/github/msohailse/incident-reporting-system/badge.svg?branch=main)](https://coveralls.io/github/msohailse/incident-reporting-system?branch=main)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=msohailse_incident-reporting-system&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=msohailse_incident-reporting-system)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=msohailse_incident-reporting-system&metric=coverage)](https://sonarcloud.io/summary/new_code?id=msohailse_incident-reporting-system)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=msohailse_incident-reporting-system&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=msohailse_incident-reporting-system)

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
# Unit tests only (no Docker needed)
mvn clean test

# Unit + integration tests (Docker starts automatically via Testcontainers)
mvn clean verify
```

## Project Structure

```
src/
  main/java/com/msohailse/app/incident/
    TransactionCode.java            functional interface for transaction lambdas
    TransactionManager.java         interface: doInTransaction(TransactionCode)
    JpaTransactionManager.java      only class calling getTransaction()
    controller/
      UserController.java           login, register
      IncidentController.java       reportIncident (find-or-create tag in one transaction)
    model/
      User.java                     JPA entity — users table
      Tag.java                      JPA entity — tags table
      Incident.java                 JPA entity — incidents table (@ManyToOne User, @ManyToOne Tag)
      Severity.java                 enum: LOW, MEDIUM, HIGH
    repository/
      IncidentReportingRepository.java   flat interface for all 3 entities
      postgres/
        IncidentReportingPostgresRepository.java   delegates to 3 sub-repos sharing one EM
        UserPostgresRepository.java
        TagPostgresRepository.java
        IncidentPostgresRepository.java
    view/
      IncidentReportingView.java         interface: showError, userLoggedIn, showAllIncidents, incidentAdded
      swing/
        IncidentReportingSwingView.java  CardLayout: LOGIN / REGISTER / MAIN / INCIDENT
    app/swing/
      IncidentReportingSwingApp.java     entry point
  test/java/com/msohailse/app/incident/
    model/        UserTest, TagTest, IncidentTest   (67 unit tests)
    controller/   UserControllerTest, IncidentControllerTest   (15 unit tests)
  it/java/com/msohailse/app/incident/
    JpaTransactionManagerIT.java         (4 IT tests)
    repository/postgres/
      UserPersistenceIT, TagPersistanceIT, IncidentPersistenceIT   (16 IT tests)
  main/resources/META-INF/persistence.xml   PostgreSQL config
docker-compose.yml                          local PostgreSQL for manual app runs only
```

## Entities

- **User** — reports incidents (firstName, lastName, email, password)
- **Incident** — reported event with title, description, severity, timestamp, isClosed; belongs to one Tag and one User
- **Tag** — single label per incident (tagTitle, tagDescription)
