**AI Finance Manager**

**Project Overview**
- AI Finance Manager is the backend service for the AI Finance project. It provides REST APIs, business logic, and persistence for accounts, transactions, budgets, categories and analytics used by the frontend.

**Tech Stack**
- Java 17+ (Spring Boot)
- Maven for build and dependency management
- Docker / Docker Compose for containerized local development

**Prerequisites**
- JDK 17 or newer installed
- Maven (optional — the wrapper is included)
- Docker & Docker Compose 

**Run Locally (Development)**
- Windows (using bundled wrapper):

  `mvnw.cmd spring-boot:run`

- macOS / Linux:

  `./mvnw spring-boot:run`

**Build & Package**
- Build a packaged JAR:

  Windows: `mvnw.cmd -DskipTests package`

  macOS / Linux: `./mvnw -DskipTests package`

- The packaged artifact will be under `target/`.

**Docker / Docker Compose**
- Build and run with Docker Compose (project root contains `docker-compose.yml`):

  `docker-compose up --build`

- Use `docker-compose down` to stop and remove containers.

**Testing**
- Run unit tests with:

  Windows: `mvnw.cmd test`

  macOS / Linux: `./mvnw test`

**Configuration / Environment**
- Configure runtime settings (database URL, credentials, etc.) using environment variables or Spring Boot properties.
- If you use a `.env` file for Docker Compose, place it alongside `docker-compose.yml`.

**Project Structure (high level)**
- `src/main/java` — application source
- `src/main/resources` — application resources and config
- `src/test` — unit and integration tests
- `pom.xml` — Maven project descriptor
- `mvnw`, `mvnw.cmd` — Maven wrapper
- `docker-compose.yml`, `Dockerfile` — containerization and deployment helpers

**Useful Commands**
- Start locally (dev): `./mvnw spring-boot:run` or `mvnw.cmd spring-boot:run`
- Build jar: `./mvnw package` or `mvnw.cmd package`
- Run tests: `./mvnw test` or `mvnw.cmd test`
- Docker compose: `docker-compose up --build`

**Contributing**
- Open a pull request with a clear description and run tests locally before submitting.



----

