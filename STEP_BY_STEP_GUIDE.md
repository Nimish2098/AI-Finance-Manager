# 📋 Step-by-Step Guide: Simplify & Deploy Your Finance Manager

## 🎯 **Goal**
Simplify to core features, fix bugs, and make it deployment-ready for your portfolio.

---

## **PHASE 1: Remove Excessive Features** (30 minutes)

### Step 1.1: Delete Unnecessary Controllers
Delete these files:
- `src/main/java/com/Nimish/AIFinanceManager/controller/BudgetController.java`
- `src/main/java/com/Nimish/AIFinanceManager/controller/AccountController.java`
- `src/main/java/com/Nimish/AIFinanceManager/controller/InsightController.java`

### Step 1.2: Simplify Transaction Model
**File**: `src/main/java/com/Nimish/AIFinanceManager/model/Transaction.java`

**Change**:
- Remove `Account account` relationship
- Add `User user` relationship instead
- Remove `aiLabel` field
- Update imports: Remove `JsonBackReference`, add `JsonIgnore`

**New structure**:
```java
@ManyToOne
@JoinColumn(name="user_id")
@JsonIgnore
private User user;
```

### Step 1.3: Simplify TransactionService
**File**: `src/main/java/com/Nimish/AIFinanceManager/service/TransactionService.java`

**Remove**:
- `AccountRepository` dependency
- `RestTemplate` dependency
- `FinancialInsightRepository` dependency
- All AI/Flask API call code
- Account balance update logic

**Change**:
- `saveTransaction(Transaction transaction, Long accountId)` → `saveTransaction(Transaction transaction, Long userId)`
- Remove account finding logic
- Just link transaction to user and save

**Add**:
- New method: `getTransactionsByUser(Long userId)`

### Step 1.4: Update TransactionRepository
**File**: `src/main/java/com/Nimish/AIFinanceManager/repository/TransactionRepository.java`

**Add**:
```java
List<Transaction> findByUserId(Long userId);
```

### Step 1.5: Update AnalysisService
**File**: `src/main/java/com/Nimish/AIFinanceManager/service/AnalysisService.java`

**Fix bugs**:
1. Change `"credit"` → `"Income"` (line 22)
2. Change `"debit"` → `"Expense"` (line 27)
3. Change `getSummary()` → `getSummary(Long userId)` - add userId parameter
4. Change `getInsights()` → `getInsights(Long userId)` - add userId parameter
5. Fix line 53: Change `"Savings Percent"` → `"savingPercent"`
6. Use `getTransactionsByUser(userId)` instead of `getAllTransaction()`

### Step 1.6: Update TransactionController
**File**: `src/main/java/com/Nimish/AIFinanceManager/controller/TransactionController.java`

**Changes**:
- Remove unused `@Autowired` import
- Add `@CrossOrigin(origins = "*")`
- Change endpoints:
  - `POST /add/{id}` → `POST /{userId}` (change param name)
  - `GET /` → `GET /user/{userId}` (get user's transactions)
  - `POST /update/{id}` → `PUT /{id}` (use PUT instead of POST)
  - `GET /summary` → `GET /summary/{userId}` (add userId param)
  - `GET /insights` → `GET /insights/{userId}` (add userId param)

### Step 1.7: Update AuthController
**File**: `src/main/java/com/Nimish/AIFinanceManager/controller/AuthController.java`

**Changes**:
- Remove `@Autowired`, use `@RequiredArgsConstructor` instead
- Rename `userService` → `authService`
- Add `@CrossOrigin(origins = "*")`

---

## **PHASE 2: Fix Critical Bugs** (1 hour)

### Step 2.1: Fix JWT Validation Bug
**File**: `src/main/java/com/Nimish/AIFinanceManager/config/JwtUtil.java`

**Line 33**: Change `parseClaimsJwt(token)` → `parseSignedClaims(token)`

**Also fix error handling**:
- Don't throw RuntimeException on errors
- Return `false` instead (for invalid tokens)

### Step 2.2: Fix AnalysisService Key Bug
**File**: `src/main/java/com/Nimish/AIFinanceManager/service/AnalysisService.java`

**Line 53**: Change `"Savings Percent"` → `"savingPercent"`

### Step 2.3: Fix Transaction Type Mismatch
**File**: `src/main/java/com/Nimish/AIFinanceManager/service/AnalysisService.java`

**Lines 22, 27**: 
- Change `"credit"` → `"Income"`
- Change `"debit"` → `"Expense"`

**Note**: Make sure TransactionService uses "Income"/"Expense" (it already does)

---

## **PHASE 3: Add JWT Security** (1-2 hours)

### Step 3.1: Create JWT Filter
**Create new file**: `src/main/java/com/Nimish/AIFinanceManager/config/JwtAuthenticationFilter.java`

**Code**:
```java
package com.Nimish.AIFinanceManager.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) 
            throws ServletException, IOException {
        
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        String email = jwtUtil.extractUsername(token);

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (jwtUtil.isTokenValid(token)) {
                UsernamePasswordAuthenticationToken authToken = 
                    new UsernamePasswordAuthenticationToken(email, null, new ArrayList<>());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
```

### Step 3.2: Update SecurityConfig
**File**: `src/main/java/com/Nimish/AIFinanceManager/config/SecurityConfig.java`

**Add**:
- Import `JwtAuthenticationFilter`
- Add filter to SecurityFilterChain
- Protect endpoints (except `/auth/**`)

**New code**:
```java
@Configuration  
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .httpBasic(httpBasic -> httpBasic.disable())
                .formLogin(form -> form.disable());

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception{
        return config.getAuthenticationManager();
    }
}
```

**Add import**:
```java
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import lombok.RequiredArgsConstructor;
```

---

## **PHASE 4: Environment Variables** (30 minutes)

### Step 4.1: Update application.properties
**File**: `src/main/resources/application.properties`

**Change from**:
```properties
spring.datasource.password=Ni@12345
```

**To**:
```properties
spring.datasource.url=jdbc:mysql://${DB_HOST:localhost}:${DB_PORT:3306}/${DB_NAME:financeproject_db}
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD:}
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
```

### Step 4.2: Update JwtUtil for Environment Variable
**File**: `src/main/java/com/Nimish/AIFinanceManager/config/JwtUtil.java`

**Add**:
```java
@Value("${jwt.secret:secretkeyshouldbebiggerthan256bitsyoukonw}")
private String secret;
```

**Change**:
```java
private final Key key = Keys.hmacShaKeyFor(secret.getBytes());
```

**Add import**:
```java
import org.springframework.beans.factory.annotation.Value;
```

---

## **PHASE 5: Create Docker Setup** (2 hours)

### Step 5.1: Create Dockerfile
**Create**: `Dockerfile` in root directory

**Content**:
```dockerfile
FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/AI-Finance-Manager-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Step 5.2: Create docker-compose.yml
**Create**: `docker-compose.yml` in root directory

**Content**:
```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    container_name: finance-mysql
    environment:
      MYSQL_ROOT_PASSWORD: ${DB_PASSWORD:-rootpassword}
      MYSQL_DATABASE: ${DB_NAME:-financeproject_db}
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      interval: 10s
      timeout: 5s
      retries: 5

  app:
    build: .
    container_name: finance-app
    ports:
      - "8080:8080"
    environment:
      DB_HOST: mysql
      DB_PORT: 3306
      DB_NAME: ${DB_NAME:-financeproject_db}
      DB_USERNAME: root
      DB_PASSWORD: ${DB_PASSWORD:-rootpassword}
      JWT_SECRET: ${JWT_SECRET:-secretkeyshouldbebiggerthan256bitsyoukonw}
    depends_on:
      mysql:
        condition: service_healthy
    restart: unless-stopped

volumes:
  mysql_data:
```

### Step 5.3: Create .env.example
**Create**: `.env.example` in root directory

**Content**:
```env
DB_PASSWORD=your_secure_password_here
DB_NAME=financeproject_db
JWT_SECRET=your_jwt_secret_key_here_min_256_bits
```

### Step 5.4: Create .dockerignore
**Create**: `.dockerignore` in root directory

**Content**:
```
target/
.mvn/
.idea/
*.iml
.env
.git
.gitignore
```

---

## **PHASE 6: Update README** (30 minutes)

### Step 6.1: Update README.md
**File**: `README.md`

**Add sections**:
1. **Simplified Features** (remove budget, account mentions)
2. **API Endpoints** (list all 8 endpoints)
3. **Setup Instructions**
4. **Docker Deployment**
5. **Environment Variables**

**Example structure**:
```markdown
## 🚀 Quick Start

### Prerequisites
- Java 21
- Maven
- Docker & Docker Compose (for deployment)

### Local Development
1. Clone repository
2. Create `.env` file from `.env.example`
3. Update database credentials
4. Run: `mvn spring-boot:run`

### Docker Deployment
1. Build: `mvn clean package`
2. Run: `docker-compose up -d`
3. Access: `http://localhost:8080`

## 📡 API Endpoints

### Authentication
- `POST /auth/register` - Register user
- `POST /auth/login` - Login (returns JWT)

### Transactions (Require JWT in Authorization header)
- `GET /api/transactions/user/{userId}` - Get user transactions
- `POST /api/transactions/{userId}` - Add transaction
- `PUT /api/transactions/{id}` - Update transaction
- `DELETE /api/transactions/{id}` - Delete transaction
- `GET /api/transactions/summary/{userId}` - Get summary
- `GET /api/transactions/insights/{userId}` - Get insights
```

---

## **PHASE 7: Testing** (30 minutes)

### Step 7.1: Test Locally
1. Build project: `mvn clean package`
2. Run: `mvn spring-boot:run`
3. Test endpoints with Postman/curl:
   - Register user
   - Login (get JWT token)
   - Add transaction (with JWT in header)
   - Get transactions
   - Get summary

### Step 7.2: Test Docker
1. Build: `mvn clean package`
2. Run: `docker-compose up`
3. Test same endpoints

---

## ✅ **CHECKLIST**

### Code Changes
- [ ] Removed BudgetController
- [ ] Removed AccountController
- [ ] Removed InsightController
- [ ] Updated Transaction model (User instead of Account)
- [ ] Simplified TransactionService
- [ ] Fixed AnalysisService bugs
- [ ] Updated TransactionController endpoints
- [ ] Fixed JWT validation bug
- [ ] Created JWT Filter
- [ ] Updated SecurityConfig
- [ ] Added environment variables

### Deployment
- [ ] Created Dockerfile
- [ ] Created docker-compose.yml
- [ ] Created .env.example
- [ ] Created .dockerignore
- [ ] Updated README

### Testing
- [ ] Tested locally
- [ ] Tested with Docker
- [ ] All endpoints working

---

## ⏱️ **TIME ESTIMATE**

- **Phase 1**: 30 minutes
- **Phase 2**: 1 hour
- **Phase 3**: 1-2 hours
- **Phase 4**: 30 minutes
- **Phase 5**: 2 hours
- **Phase 6**: 30 minutes
- **Phase 7**: 30 minutes

**Total: ~6-7 hours**

---

## 🎯 **PRIORITY ORDER**

1. **Must Do First**: Phase 1 & 2 (fix bugs, simplify)
2. **Then**: Phase 3 (security)
3. **Then**: Phase 4 & 5 (deployment)
4. **Finally**: Phase 6 & 7 (documentation & testing)

---

## 💡 **TIPS**

- Test after each phase
- Commit code after each phase
- Keep it simple - don't add features
- Focus on making it work, not perfect
- For portfolio, working > perfect

---

**Good luck! 🚀**

