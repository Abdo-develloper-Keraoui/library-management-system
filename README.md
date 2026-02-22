# 📚 Library Management System

> ⚠️ **This project is currently in development.** Building over 3 weeks as a portfolio project for backend-focused internship applications.

---

## What is this?

A full-stack library management system where users can browse books, borrow and return them, and admins can manage the book catalogue. Built to practice backend development with Java and Spring Boot, and to have something real to show in internship interviews.

The project covers: REST APIs, JWT authentication, role-based access control, database relationships, business rule enforcement, pessimistic locking for concurrency, and Docker deployment.

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
- [x] Borrow a book (authenticated users)
- [x] Return a book
- [x] Borrow history per user
- [x] Admin view of all borrow records (with borrower details)
- [x] Business rules — max 3 active borrows, no duplicate borrows, copies must be available
- [x] Concurrency handling with pessimistic locking (`SELECT ... FOR UPDATE`)

### 📅 Planned
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
| USER | Everything a guest can + borrow and return books, view own borrow history |
| ADMIN | Everything a user can + create/update/delete books, view all borrow records |

### Borrowing Business Rules

1. Book must have `copiesAvailable > 0`
2. User cannot have more than 3 active borrows at once
3. User cannot borrow the same book twice while it is already ACTIVE
4. Borrowing decreases `copiesAvailable` by 1; returning increases it by 1
5. Borrow period is 14 days — `dueDate` is set automatically on creation
6. Only the user who borrowed a book can return it
7. Concurrent borrow attempts on the same book are handled with pessimistic locking (`SELECT ... FOR UPDATE`). The second transaction waits until the first commits, then reads the updated copy count — preventing negative inventory.

### Database Schema

```
users                 books                   borrows
─────                 ─────                   ───────
id                    id                      id
first_name            title                   user_id  → FK
last_name             author (String)         book_id  → FK
email (unique)        isbn                    borrow_date
password (BCrypt)     pub_year                due_date
role (USER/ADMIN)     copies_available        return_date (nullable)
created_at            cover_image_url (nullable)  status (ACTIVE/RETURNED)
                      created_at
```

### API Endpoints

| Method | Endpoint | Access | Status |
|--------|----------|--------|--------|
| POST | `/api/v1/auth/register` | Public | ✅ Done |
| POST | `/api/v1/auth/login` | Public | ✅ Done |
| GET | `/api/v1/books` | Public | ✅ Done |
| GET | `/api/v1/books/{id}` | Public | ✅ Done |
| POST | `/api/v1/books` | Admin only | ✅ Done |
| PUT | `/api/v1/books/{id}` | Admin only | ✅ Done |
| DELETE | `/api/v1/books/{id}` | Admin only | ✅ Done |
| POST | `/api/v1/borrows/{bookId}/borrow` | Authenticated | ✅ Done |
| PUT | `/api/v1/borrows/{id}/return` | Authenticated | ✅ Done |
| GET | `/api/v1/borrows/my` | Authenticated | ✅ Done |
| GET | `/api/v1/borrows` | Admin only | ✅ Done |

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

> PostgreSQL runs on port **5555** (not the default 5432 — Windows reserves that range).

3. Set the JWT secret as an environment variable

On Mac/Linux:
```bash
export JWT_SECRET=your-secret-key-at-least-32-characters-long
```

On Windows (PowerShell):
```powershell
$env:JWT_SECRET="your-secret-key-at-least-32-characters-long"
```

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
│       ├── controller/        # REST controllers (Auth, Book, Borrow)
│       ├── dto/               # Data transfer objects (auth, book, borrow)
│       ├── exception/         # Custom exceptions + global handler
│       ├── model/             # JPA entities (User, Book, Borrow, Role, BorrowStatus)
│       ├── repository/        # Spring Data repositories
│       ├── security/          # JWT utils, filter, UserDetailsService
│       └── service/           # Business logic (Auth, Book, Borrow)
├── library-frontend/          # React frontend (coming Week 3)
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
  "timestamp": "2026-02-22T10:30:00"
}
```

---

## Security Notes

- Passwords are hashed with BCrypt — plain text passwords are never stored
- JWT secret is loaded from an environment variable — never hardcoded in source
- Tokens expire after 24 hours
- Sessions are stateless — no server-side session storage
- Unauthenticated requests return **401**; authenticated but unauthorized requests return **403**

---

## Notes

I'm keeping a `decisions.md` file in the root of the project documenting the reasoning behind technical choices — why pessimistic over optimistic locking, why DTOs instead of exposing entities, why a single `BorrowResponseDTO` for both user and admin endpoints, and more. Useful for interview prep and for anyone reading the code.

---

*Built by [Abdottawab KERAOUI] — learning Spring Boot and backend development.*