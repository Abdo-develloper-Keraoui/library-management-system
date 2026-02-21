> Every major decision made during this project — **what** was decided and **why**.  
> Useful for interview prep, future reference, and proving you actually thought about your choices.

---

## 📦 Scope Decisions

### 1. 👤 Author is a String field on Book — not a separate entity

**Decision:** Author is stored as a plain `String` on the `Book` entity. No `Author` table, no `Author` CRUD, no relationships.

**Why:** A dedicated `Author` entity would cost 4–6 hours and demonstrate zero new architectural concepts beyond what Book CRUD already covers. In a 70-hour project, that time is better spent on authentication, borrowing logic, and deployment.

---

### 2. 📅 Borrow statuses: ACTIVE and RETURNED only — no OVERDUE

**Decision:** `BorrowStatus` enum has two values: `ACTIVE` and `RETURNED`.

**Why:** `OVERDUE` requires either a scheduled background job or on-read calculation logic — both add complexity disproportionate to the portfolio value gained. Two statuses are enough to demonstrate state management in a borrowing flow. Can be added later as an enhancement.

---

### 3. 🔒 Concurrency handled with pessimistic locking only

**Decision:** Borrow operations use `SELECT ... FOR UPDATE` (pessimistic locking).

**Why:** Prevents two users from borrowing the last copy simultaneously. Pessimistic locking is the simplest correct solution for this problem. Optimistic locking with version columns is an alternative, but pessimistic is easier to implement and explain in interviews.

---

### 4. 🖼️ Book cover image stored as a nullable URL string — no file upload

**Decision:** `Book` has a nullable `coverImageUrl` String field. No multipart upload, no file storage.

**Why:** A cover image makes the demo look significantly better. Storing it as a URL costs 5 minutes. Actual file upload (multipart requests, S3/disk storage, serving files) would cost 4–6 hours and introduces infrastructure complexity irrelevant to a Library Management System's core purpose. Nullable because not every book entry needs an image.

---

### 5. 🔢 `copiesAvailable` as an int — no separate boolean availability flag

**Decision:** Availability is derived directly from `copiesAvailable > 0`. No `isAvailable` boolean.

**Why:** Two fields representing the same thing will eventually get out of sync and cause bugs. A boolean is redundant — you can derive it from the int. Redundant state is always a liability.

---

## 🛠️ Technology Decisions

### 6. ⚛️ React + Vite for frontend — not Next.js

**Decision:** Frontend is built with plain React + Vite.

**Why:** Zero JavaScript experience going in. Next.js adds SSR, file-based routing, and its own abstractions — three layers of new concepts at once. React + Vite means only learning components, state, and API calls. The frontend is ~20% of this project; the backend is where the interview value lives.

---

### 7. 🔐 Spring Security + JWT — not BetterAuth

**Decision:** Authentication uses Spring Security with JWT tokens.

**Why:** BetterAuth is a JavaScript/TypeScript library — it doesn't work with Java/Spring Boot. In the Java ecosystem, Spring Security + JWT is the industry standard. This is what recruiters and interviewers expect to see on a Java backend project.

---

### 8. 🐳 Only PostgreSQL runs in Docker during development

**Decision:** PostgreSQL runs in Docker. Spring Boot runs from IntelliJ. React runs via `npm run dev`.

**Why:** Fast feedback loops during development. Hot reload for both backend and frontend. Only the database is infrastructure — it belongs in a container. Everything gets containerized at deployment (Day 18). This mirrors how real development teams work.

---

### 9. 📦 Full Docker Compose setup is a deployment-phase task — Day 18

**Decision:** Dockerfiles and `docker-compose.yml` are written once the application is feature-complete.

**Why:** Writing Docker configs during active development adds unnecessary build cycles. It's a deployment concern, not a dev concern. Doing it at the end proves we understand containerization without slowing down daily progress.

---

### 10. ⚙️ CI/CD with GitHub Actions — minimal pipeline

**Decision:** A basic GitHub Actions pipeline runs build + tests on every push to `main`.

**Why:** Takes ~1–2 hours, sends a strong signal to recruiters that we understand modern development workflows. Kept simple — no multi-stage deployments or complex configurations.

---

### 11. 🔌 PostgreSQL on port 5555

**Decision:** PostgreSQL container mapped to host port 5555 instead of default 5432.

**Why:** Windows/Hyper-V reserves ports 5431–5530 (includes 5432 and 5433). Port 5555 is outside all excluded ranges.

---

### 12. 🌐 Spring Boot on port 8081

**Decision:** Spring Boot runs on port 8081 instead of default 8080.

**Why:** Port 8080 was already occupied on the development machine. Port 8081 is used during development only.

---

## 🏗️ Architecture Decisions

### 13. 🧱 Layered architecture: Controller → Service → Repository

**Decision:** Standard three-layer Spring Boot architecture.

**Why:** Controllers handle HTTP (request/response mapping). Services handle business logic. Repositories handle data access. Clean separation of concerns makes the code testable, maintainable, and easy to explain. This is what interviewers expect from a Java backend project.

---

### 14. 🎯 Backend-focused project with minimal frontend

**Decision:** Deep backend, shallow frontend.

**Why:** Targeting backend/fullstack internships with a backend emphasis. The frontend needs to prove API integration, client-side auth, and basic UI — it does NOT need to be beautiful. Time is better invested in solid backend logic, error handling, and deployment.

---

### 15. 💥 Service methods throw exceptions on "not found" — never return Optional

**Decision:** Services throw `ResourceNotFoundException` instead of returning `Optional`.

**Why:** In a REST API, "not found" is an error — the correct response is 404, not 200 with an empty body. Returning `Optional` from a service pushes the decision up to the controller, so every controller must handle it separately. Throwing an exception lets `GlobalExceptionHandler` catch it once, in one place, and return a consistent 404 everywhere.

---

### 16. 🚨 Custom exception classes — not generic RuntimeException

**Decision:** `ResourceNotFoundException`, `BusinessException` instead of raw `RuntimeException`.

**Why:** A generic `RuntimeException` gives Spring no information about what went wrong — it defaults to 500 regardless. Custom exceptions carry meaning and let `GlobalExceptionHandler` map each one to the correct HTTP status (404, 400). One place to change, consistent behaviour everywhere.

---

### 17. ✅ `existsById()` used in deleteBook — not `findById()`

**Decision:** `existsById()` to check existence before delete, not `findById()`.

**Why:** The only purpose of the lookup is confirming the book exists. Fetching the full entity just to get its id back is wasteful — we already have the id. `existsById()` hits the database once, returns a boolean, avoids an unused variable.

---

### 18. 📍 `@RequestMapping` at the class level in controllers

**Decision:** Base path defined once at the class level, not repeated on every method.

**Why:** Repeating `/api/v1/books` on every method is unnecessary duplication. If the path changes, you'd have to update every method. Class-level `@RequestMapping` means one change propagates everywhere.

---

### 19. 🔁 `mapToDTO()` private helper method in BookService

**Decision:** Single private `mapToDTO()` method instead of repeating the DTO constructor.

**Why:** Without it, the same `new BookResponseDTO(...)` call is repeated four times. If a field is ever added to `BookResponseDTO`, you'd have to find and update four places. Extracting it once is the DRY principle — Don't Repeat Yourself.

---

### 20. 🔑 Password hashing in the service layer — not controller or entity

**Decision:** BCrypt hashing happens in `AuthService`, not `AuthController` or `User`.

**Why:** The service layer is where business logic lives. If hashing were done in the controller, every controller that creates a user would have to remember to hash — that's duplication and a security risk. In the service, it happens once, every time, in one place.

---

### 21. 🛡️ Validation errors handled in GlobalExceptionHandler — not per-controller

**Decision:** `MethodArgumentNotValidException` is caught once in `GlobalExceptionHandler`.

**Why:** Without a central handler, Spring returns its own inconsistent error format. Catching it once means every endpoint returns the same `ErrorResponse` shape on validation failure. One place to change, consistent behaviour everywhere.

---

### 22. 🔒 Role-based authorization: SecurityConfig for broad rules + @PreAuthorize for fine-grained rules

**Decision:** Authorization uses both `SecurityConfig` and `@PreAuthorize` together — not one or the other.

`SecurityConfig` handles broad, structural rules:
- Auth endpoints are public
- GET book endpoints are public
- Everything else requires authentication

`@PreAuthorize("hasRole('ADMIN')")` handles fine-grained rules on controller methods:
- `POST /api/v1/books` — ADMIN only
- `PUT /api/v1/books/{id}` — ADMIN only
- `DELETE /api/v1/books/{id}` — ADMIN only

**Why:** Putting all rules in `SecurityConfig` centralizes them but creates a hidden coupling problem — a developer reading `BookController.java` has no idea who can call each method without opening a separate file. As the project grows, `SecurityConfig` becomes a long list of URL patterns that's increasingly fragile (Spring evaluates matchers in order; getting the order wrong silently breaks rules).

`@PreAuthorize` on the controller puts the authorization rule at the point of definition — right next to the method it protects. This is immediately readable and scales cleanly as new controllers and endpoints are added.

The two layers serve different purposes and work best together. This is the industry-standard approach in Spring Boot projects.

---

## 🔐 Concepts — Security & JWT

### What is a JWT?

When a user logs in, the server needs a way to "remember" them on future requests. But this is a stateless REST API — no sessions. So instead, the server hands the client a **signed piece of paper** that says "this is who you are."

That piece of paper is a **JWT — JSON Web Token**.

```
eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyQGdtYWlsLmNvbSJ9.abc123xyz
        HEADER                        PAYLOAD                  SIGNATURE
```

| Part | Contents |
|------|----------|
| **Header** | Algorithm used to sign (e.g. HS256) |
| **Payload** | Data — email, role, expiry. Base64 encoded, NOT encrypted — anyone can read it |
| **Signature** | Cryptographic hash of header + payload, signed with the server's secret key |

> ⚠️ **The signature is the key insight.** If someone tampers with the payload, the signature breaks. The server detects it instantly.

---

### The Full JWT Flow

```
1. POST /api/v1/auth/login  {email, password}
         ↓
2. AuthService verifies password with BCrypt
         ↓
3. JwtUtils generates a signed token
         ↓
4. Server returns: { "token": "eyJhbG..." }
         ↓
5. Client stores the token
         ↓
6. Every subsequent request:
   Authorization: Bearer eyJhbG...
         ↓
7. JwtAuthenticationFilter reads + validates the token,
   extracts email, loads user, sets SecurityContextHolder
         ↓
8. SecurityConfig allows or denies based on role
```

---

### The 6 Things Built for JWT on Day 7

| Thing | Purpose |
|-------|---------|
| `JwtUtils` | Generate a token, validate a token, extract email from token |
| `CustomUserDetailsService` | Load user from DB by email — Spring Security contract |
| `JwtAuthenticationFilter` | Runs on every request — reads, validates, sets auth context |
| `login()` in AuthService | Verify password, call JwtUtils, return real token |
| `POST /auth/login` | Wire it up in AuthController |
| Updated `SecurityConfig` | Register filter, stateless sessions, define public vs protected |

---

### Secret Key — Environment Variable

- Secret key is **NOT hardcoded** in `JwtUtils.java` — anyone with the key can forge tokens
- Stored in `application.properties` as `${JWT_SECRET}` placeholder
- Actual value set in IntelliJ run configuration (local) and Docker Compose env block (deployment)
- The real value **never touches the codebase**

---

### Authentication vs Authorization

| Concept | Question it answers | Where it lives |
|---------|-------------------|----------------|
| **Authentication** | Who are you? | `JwtAuthenticationFilter` |
| **Authorization** | What are you allowed to do? | `SecurityConfig` + `@PreAuthorize` |

> The filter identifies. The config decides. They are separate concerns.

---

## 🔒 Locked MVP Feature Set

| Feature | Status |
|---------|--------|
| Register / Login (JWT) | ✅ Done |
| Role-based access: USER and ADMIN | ✅ Done |
| Book CRUD (Admin only) | ✅ Done |
| Public book browsing | ✅ Done |
| Borrow a book / Return a book | 📅 Days 10–11 |
| Borrow status tracking (ACTIVE / RETURNED) | 📅 Days 10–11 |
| Business rules: max 3 borrows, no duplicates | 📅 Days 10–11 |
| Global exception handling + validation | ✅ Done |
| Swagger API documentation | 📅 Day 12 |
| Docker Compose for deployment | 📅 Day 18 |
| Deployed to free hosting platform (live URL) | 📅 Day 19 |
| Basic CI/CD pipeline (GitHub Actions) | 📅 Day 18 |
| README + this decision log | ✅ In progress |

---

_Last updated: Day 8 ✅_