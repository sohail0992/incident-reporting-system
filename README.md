# Incident Reporting System
Incident reporting system built with Java, JPA/Hibernate, PostgreSQL, Swing UI, and tested with TDD.

[![Build Status](https://github.com/msohailse/incident-reporting-system/actions/workflows/maven.yml/badge.svg)](https://github.com/msohailse/incident-reporting-system/actions)
[![Coverage Status](https://coveralls.io/repos/github/msohailse/incident-reporting-system/badge.svg?branch=main)](https://coveralls.io/github/msohailse/incident-reporting-system?branch=main)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=com.msohailse.app%3Aincident&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=com.msohailse.app%3Aincident)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=com.msohailse.app%3Aincident&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=com.msohailse.app%3Aincident)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=com.msohailse.app%3Aincident&metric=bugs)](https://sonarcloud.io/summary/new_code?id=com.msohailse.app%3Aincident)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=com.msohailse.app%3Aincident&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=com.msohailse.app%3Aincident)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=com.msohailse.app%3Aincident&metric=coverage)](https://sonarcloud.io/summary/new_code?id=com.msohailse.app%3Aincident)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=com.msohailse.app%3Aincident&metric=duplicated_lines_density)](https://sonarcloud.io/summary/new_code?id=com.msohailse.app%3Aincident)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=com.msohailse.app%3Aincident&metric=sqale_index)](https://sonarcloud.io/summary/new_code?id=com.msohailse.app%3Aincident)

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
cd com.msohailse.app.incident

# Unit tests only (no Docker needed)
mvn clean test

# Unit + UI + IT + E2E (Docker starts automatically via Testcontainers)
mvn clean verify

# Unit + UI + Mutation + IT + E2E
mvn verify -Pmutation-testing
```

