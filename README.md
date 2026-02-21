# 📚 Library Management System

> ⚠️ **This project is currently in development.** Building over 3 weeks as a portfolio project while learning Spring Boot.

---

## What is this?

A full-stack library management system where users can browse books, borrow and return them, and admins can manage the book catalogue. Built to practice backend development with Java and Spring Boot, and to have something real to show in internship interviews.

The project covers: REST APIs, JWT authentication, role-based access control, database relationships, pessimistic locking for concurrency, and Docker deployment.

---

## Tech Stack

**Backend**
- Java 21
- Spring Boot 4.0.x
- PostgreSQL 17
- Spring Security + JWT (jjwt 0.12.6)
- Spring Data JPA / Hibernate

**Frontend** *(in progress — Week 3)*
- React + Vite

**Infrastructure** *(planned — Week 3)*
- Docker Compose
- GitHub Actions CI/CD
- Render / Railway deployment

---

## Features

### ✅ Done
- [x] Book CRUD (create, read, update, delete)
- [x] Input validation and global error handling
- [x] Consistent JSON error responses
- [x] User registration with BCrypt password hashing
- [x] JWT authentication — login returns a signed token
- [x] JWT filter — validates token on every request
- [x] Stateless session management
- [x] Role-based authorization — ADMIN and USER roles enforced
- [x] Admin-only book write endpoints (POST, PUT, DELETE)
- [x] Correct 401 / 403 distinction — unauthenticated vs unauthorized

### 📅 Planned
- [ ] Borrow and return books
- [ ] Borrow history per user
- [ ] Max 3 active borrows per user
- [ ] Concurrency handling with pessimistic locking
- [ ] Swagger API docs
- [ ] React frontend
- [ ] Docker Compose setup
- [ ] CI/CD pipeline
- [ ] Cloud deployment

---

## System Design

The backend follows a standard layered architecture:

```
Controller  →  Service  →  Repository  →  Database
```

- **Controller** — handles HTTP requests and responses, nothing else
- **Service** — all business logic lives here
- **Repository** — talks to the database via Spring Data JPA
- **DTOs** — separate classes for input and output (entities are never exposed directly)
- **Security** — JWT filter intercepts every request, validates token, sets auth context

### Authentication Flow

```
POST /api/v1/auth/login  {email, password}
        ↓
Server verifies password with BCrypt
        ↓
Server generates signed JWT token
        ↓
Client stores token, sends it on every request:
Authorization: Bearer <token>
        ↓
JwtAuthenticationFilter validates token → sets SecurityContext
        ↓
SecurityConfig + @PreAuthorize enforce access by role
```

### Authorization Model

Two layers work together:

| Layer | Responsibility |
|-------|---------------|
| `SecurityConfig` | Broad rules — public vs authenticated |
| `@PreAuthorize` | Fine-grained rules — ADMIN vs USER per endpoint |

| Role | Can do |
|------|--------|
| Guest (no token) | Browse books, register, login |
| USER | Everything a guest can + borrow and return books |
| ADMIN | Everything a user can + create, update, delete books |

### Database Schema

```
users                 books                 borrows
─────                 ─────                 ───────
id                    id                    id
first_name            title                 user_id  → FK
last_name             author (String)       book_id  → FK
email (unique)        isbn                  borrow_date
password (BCrypt)     pub_year              due_date
role (USER/ADMIN)     copies_available      return_date (nullable)
created_at            cover_image_url       status (ACTIVE/RETURNED)
                      created_at
```

### API Endpoints

| Method | Endpoint | Access | Status |
|--------|----------|--------|--------|
| GET | `/api/v1/books` | Public | ✅ Done |
| GET | `/api/v1/books/{id}` | Public | ✅ Done |
| POST | `/api/v1/books` | Admin only | ✅ Done |
| PUT | `/api/v1/books/{id}` | Admin only | ✅ Done |
| DELETE | `/api/v1/books/{id}` | Admin only | ✅ Done |
| POST | `/api/v1/auth/register` | Public | ✅ Done |
| POST | `/api/v1/auth/login` | Public | ✅ Done |
| POST | `/api/v1/borrows/{bookId}/borrow` | User | 📅 Planned |
| PUT | `/api/v1/borrows/{id}/return` | User | 📅 Planned |
| GET | `/api/v1/borrows/my` | User | 📅 Planned |
| GET | `/api/v1/borrows` | Admin | 📅 Planned |

---

## Running Locally

### Prerequisites
- Java 21
- Docker Desktop (for PostgreSQL)
- Maven

### Steps

1. Clone the repo
```bash
git clone https://github.com/yourusername/library-system.git
cd library-system
```

2. Start the database
```bash
docker-compose up -d
```

3. Set the JWT secret as an environment variable
```bash
export JWT_SECRET=your-secret-key-at-least-32-characters-long
```
> On Windows (PowerShell): `$env:JWT_SECRET="your-secret-key"`

4. Run the backend
```bash
cd library-management
./mvnw spring-boot:run
```

The API will be available at `http://localhost:8081`

---

## Project Structure

```
library-system/
├── library-management/        # Spring Boot backend
│   └── src/main/java/com/library/library_management/
│       ├── config/            # Security configuration
│       ├── controller/        # REST controllers
│       ├── dto/               # Data transfer objects (auth, book, borrow)
│       ├── exception/         # Custom exceptions + global handler
│       ├── model/             # JPA entities (User, Book, Role)
│       ├── repository/        # Spring Data repositories
│       ├── security/          # JWT utils, filter, UserDetailsService
│       └── service/           # Business logic (Auth, Book)
├── library-frontend/          # React frontend (coming soon)
├── docker-compose.yml
├── decisions.md               # Technical decision log
└── README.md
```

---

## Error Handling

All errors return a consistent JSON format:

```json
{
  "status": 404,
  "message": "Book not found with id: 5",
  "timestamp": "2026-02-21T10:30:00"
}
```

---

## Security Notes

- Passwords are hashed with BCrypt before storage — plain text passwords are never saved
- JWT secret is loaded from an environment variable — never hardcoded in source
- Tokens expire after 24 hours
- Sessions are stateless — no server-side session storage
- Unauthenticated requests return **401**; authenticated but unauthorized requests return **403**

---

## Notes

I'm keeping a `decisions.md` file in the root of the project documenting why I made certain choices — why pessimistic over optimistic locking, why constructor injection, why DTOs over exposing entities, etc. Useful for interview prep.

---

*Built by [Abdottawab KERAOUI] — learning Spring Boot and backend development.*