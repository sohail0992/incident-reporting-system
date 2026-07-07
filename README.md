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

