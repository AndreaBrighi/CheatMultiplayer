Integration & Test guide

Overview
- The project uses a version catalog (`gradle/libs.versions.toml`) for plugin and dependency versions.
- Testcontainers are configured for integration tests (Postgres), but the integration tests fall back to H2 if Docker is not available.
- Flyway is used to manage DB migrations; migration scripts are in `src/main/resources/db/migration`.

Run integration test (single):

```bash
# run only the ApplicationIntegrationTest
./gradlew test --tests org.example.server.ApplicationIntegrationTest --info
```

Run all tests:

```bash
./gradlew test --info
```

Use Testcontainers (Postgres) locally:
- Make sure Docker Desktop is running.
- The integration test will attempt to start a Postgres container; if Docker isn't available, it will use H2 instead.

Flyway
- Migration scripts live in `src/main/resources/db/migration` and are applied at application startup (via Flyway plugin/config).
- To run Flyway migrations explicitly via Gradle:

```bash
./gradlew flywayMigrate -Dspring.datasource.url=jdbc:postgresql://localhost:5432/yourdb -Dspring.datasource.username=youruser -Dspring.datasource.password=yourpass
```

Notes
- Keep catalog versions in `gradle/libs.versions.toml` up-to-date. You can add more libraries to the catalog for consistency.
- For realistic integration tests prefer running with Docker/Testcontainers in CI to match the production DB behavior.

