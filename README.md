# Fitness Core - Backend

This is the backend API for Fitness Core, built with Spring Boot, Java 25, and Maven.

---

## Getting Started

### Local Setup
1. Ensure Java JDK 25 is installed.
2. Copy `.env.template` to `.env` and fill in the values:
   ```bash
   cp .env.template .env
   ```
3. Run the application using the Maven wrapper:
   ```bash
   ./mvnw spring-boot:run
   ```

### Running Tests
To run unit and integration tests:
```bash
./mvnw clean test
```

---

## Secret Protection Guidelines
1. **No secrets in git**: All credentials (database connections, private keys, API secrets) must be loaded from environment variables or a local `.env` file (which is ignored by Git).
2. **Adding settings**: When adding new configuration parameters that contain secrets, define them in `application.yml` using placeholders (e.g., `my.secret: ${SECRET_ENV_VAR:default_safe_value}`) and document the environment variable in `.env.template`.

---

## Documentation & Changelog Rules

All changes in the backend must be documented here in the Changelog section using standard Keep a Changelog formatting.

### Changelog

All notable changes to this project will be documented in this section.

#### [Unreleased]
- No unreleased changes yet.

#### [0.0.1] - 2026-06-02
##### Added
- `feat(core)` Initial Spring Boot application setup with Java 25.
- `feat(database)` Integrated Spring Data JPA and PostgreSQL configurations.
- `feat(endpoint)` Added `/api/ping` status endpoint for health-checking.
- `test(endpoint)` Added controller unit tests for `PingController`.

---

## License

This project is open-source and licensed under the **MIT License**. See the [LICENSE](file:///D:/repos/fitness-core/backend/LICENSE) file for more details.

