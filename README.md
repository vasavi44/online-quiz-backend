# 🚀 Online Quiz Application — Backend API

A secure, high-performance RESTful backend for the Online Quiz Application built with **Java 21**, **Spring Boot 3.3.5**, **Spring Security 6**, and **Spring Data JPA**.

---

## 🌟 Key Features

* **JWT Stateless Authentication**: Secure registration and login using HMAC-SHA256 Bearer tokens.
* **BCrypt Password Hashing**: Zero plain-text credentials stored or returned.
* **Anti-Cheating Security**:
  * Correct answers (`isCorrect`) are **never exposed** in the public `/api/quiz/questions` endpoint.
  * Quiz scoring and evaluation are performed strictly on the server.
* **10 Curated Pre-Seeded MCQs**: Auto-seeded on startup via `DataInitializer` (idempotent).
* **Detailed Post-Quiz Breakdown**: Evaluates question-by-question responses and returns correct answers only after submission.
* **Student History Persistence**: Tracks all user attempts with timestamps, scores, and percentages.
* **Production Ready**: Pre-configured for **Render** (dynamic `$PORT`, Render PostgreSQL support, and multi-stage `Dockerfile`).

---

## 🛠️ Tech Stack

* **Language**: Java 21+
* **Framework**: Spring Boot 3.3.5
* **Security**: Spring Security 6, JJWT 0.12.6, BCrypt
* **Database**: PostgreSQL (Production) / Embedded H2 in PostgreSQL mode (Local dev fallback)
* **Build Tool**: Apache Maven (with Maven Wrapper `mvnw`)
* **Containerization**: Docker (multi-stage)

---

## 📡 REST API Reference

### Authentication
* `POST /api/auth/register` — Register student (`name`, `email`, `password`)
* `POST /api/auth/login` — Login student and receive JWT token
* `GET /api/auth/me` — Get current user profile (requires Bearer token)

### Quiz
* `GET /api/quiz/questions` — Retrieve 10 questions (**omits `isCorrect`**)
* `POST /api/quiz/submit` — Submit answers, calculate score, record attempt, return review
* `GET /api/quiz/history` — Retrieve authenticated user's past quiz attempts

### System
* `GET /api/health` — Service health check

---

## 🚀 Local Development

### Prerequisites
* JDK 21 or higher (`java -version`)
* (Optional) Maven (or use `./mvnw`)

### Running Locally
```bash
# 1. Run with Maven Wrapper
./mvnw spring-boot:run
```
The server will start on `http://localhost:8080`.

### Running Tests
```bash
./mvnw clean test
```

### Packaging JAR
```bash
./mvnw clean package -DskipTests
java -jar target/online-quiz-application-1.0.0.jar
```

---

## ⚙️ Environment Variables

| Variable | Description | Default / Example |
| :--- | :--- | :--- |
| `PORT` | Dynamic server port (Render) | `8080` |
| `DATABASE_URL` | Auto-provided by Render PostgreSQL | `postgres://user:pass@host:5432/dbname` |
| `SPRING_DATASOURCE_URL` | Explicit JDBC URL | `jdbc:postgresql://localhost:5432/quizdb` |
| `SPRING_DATASOURCE_USERNAME` | Database username | `postgres` |
| `SPRING_DATASOURCE_PASSWORD` | Database password | `your_password` |
| `JWT_SECRET` | Secret key for JWT (32+ chars) | `your-secret-key-at-least-32-chars-long` |
| `CORS_ALLOWED_ORIGINS` | Permitted frontend origins | `http://localhost:5173,https://your-frontend.vercel.app` |

---

## 🚢 Deploying to Render

1. Create a PostgreSQL database on [Render](https://dashboard.render.com/).
2. Create a **New Web Service** pointing to this repository.
3. Select **Docker** runtime (uses `Dockerfile`).
4. Set environment variables:
   * `DATABASE_URL`: *(PostgreSQL URL from Render)*
   * `JWT_SECRET`: *(32+ character random string)*
   * `CORS_ALLOWED_ORIGINS`: `https://<your-frontend-app>.vercel.app,http://localhost:5173`
5. Deploy service and copy your public URL.
