# Incident Reporting System (IS)

[![Java](https://img.shields.io/badge/Java-17-blue)](https://www.oracle.com/java/)

> A clean Maven-based Java 17 project with CLI, JavaFX GUI, E2E and BDD tests using JUnit 5, Cucumber, and PostgreSQL — wired with Google Guice and built for CI with GitHub Actions and PIT mutation testing.

---

## 📌 Features

- SQL backend with PostgreSQL + Hibernate (JPA)
- Configurable at runtime using Google Guice
- Command-Line Interface (CLI) and JavaFX GUI
- BDD testing with Cucumber (Gherkin)
- GitHub Actions CI/CD pipeline
- Clean modular structure inspired by [Bettini’s TDD Book]([https://github.com/LorenzoBettini/tddbook-code](https://github.com/LorenzoBettini/tdd-buildautomation-ci-book-examples))
---

## 📋 Requirements

| Tool            |
|-----------------|
| Java 17         |
| Maven           |
| PostgreSQL      |
| Hibernate (JPA) |
| Guice           |
| JavaFX          |
| JUnit 5         |
| Cucumber        |
| PIT             |
| Git + GitHub    |
| Docker          |

---

## 🏁 Getting Started

```bash
# Clone the repo
git clone https://github.com/yourusername/incident-reporting-system.git
cd incident-reporting-system

# Build and test
mvn clean install

# Run app (CLI or GUI based on entry)
mvn exec:java
````

---

## 🧪 Testing

```bash
# Unit + integration tests
mvn test

# BDD (Cucumber)
mvn verify

# Mutation testing
mvn org.pitest:pitest-maven:mutationCoverage
```

---

## 🚀 CI/CD

* **GitHub Actions** runs on every push
* **SonarCloud** handles code quality + test coverage
* **Docker** used for PostgreSQL container if needed in CI

---
