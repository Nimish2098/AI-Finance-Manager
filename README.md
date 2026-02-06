# Finance App Backend

A robust Spring Boot backend for the Personal Finance Tracker application, providing secure API endpoints for financial data management.
# Live Demo Line : 
https://frontendfinapp.vercel.app/
## 🚀 Technologies

- **Java**: 21
- **Framework**: Spring Boot
- **Database**: PostgreSQL
- **Security**: Spring Security with JWT Authentication
- **Documentation**: SpringDoc OpenAPI (Swagger UI)
- **Tools**: Maven, Lombok

## 📋 Prerequisites

- Java 21 or higher
- Maven
- PostgreSQL Database running locally

## ER Diagram
[![View on Eraser](https://app.eraser.io/workspace/u1auprUp60SpSpITMV22/preview?diagram=gK-914SqUivyQa4j1hL0L&type=embed)](https://app.eraser.io/workspace/u1auprUp60SpSpITMV22?diagram=gK-914SqUivyQa4j1hL0L)
## ⚙️ Configuration

The application is configured to connect to a local PostgreSQL database. Ensure your database is running and verify the settings in `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/finance_db
    username: finance_user
    password: finance_pass
server:
  port: 8080
```

### JWT Configuration
Update the JWT secret in `application.yml` for production use:
```yaml
jwt:
  secret: your_secure_secret_key_here
```

## 🛠️ Installation & Running

1. **Clone the repository** (if not already done).
2. **Navigate to the backend directory**:
   ```bash
   cd financeApp
   ```
3. **Build the project**:
   ```bash
   ./mvnw clean install
   ```
4. **Run the application**:
   ```bash
   ./mvnw spring-boot:run
   ```

The server will start on `http://localhost:8080`.

## 📚 API Documentation

Once the application is running, you can access the interactive API documentation (Swagger UI) at:

```
http://localhost:8080/swagger-ui.html
```

## 🔒 Security

- The API is secured using JWT (JSON Web Tokens).
- Public endpoints: Authentication (`/auth/**`) likely, and Swagger UI.
- Protected endpoints require a valid Bearer token in the `Authorization` header.

## 🤝 Contributing

1. Fork the Project
2. Create your Feature Branch
3. Commit your Changes
4. Push to the Branch
5. Open a Pull Request
