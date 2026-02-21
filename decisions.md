> This document tracks every major architectural and scope decision made during the project. Each entry explains **what** was decided and **why** — useful for interview prep and future reference.

---

## Scope Decisions

### 1. Author is a String field on Book, not a separate entity

**Why:** A dedicated Author entity with its own CRUD, relationships, and endpoints would cost 4–6 hours of development time. It demonstrates no new architectural concept beyond what Book CRUD already covers. In a 70-hour project, that time is better spent on authentication, borrowing logic, and deployment.

### 2. Borrow statuses limited to ACTIVE and RETURNED (no OVERDUE)

**Why:** OVERDUE requires either a scheduled background job or on-read calculation logic. Both add complexity that's disproportionate to the portfolio value gained. Two statuses are enough to demonstrate state management in a borrowing flow. Can be added later as an enhancement.

### 3. Concurrency handled with pessimistic locking only

**Why:** We need to prevent two users from borrowing the last copy of a book simultaneously. Pessimistic locking (SELECT ... FOR UPDATE) is the simplest correct solution. Optimistic locking with version columns is an alternative, but pessimistic is easier to implement and explain in interviews. We're solving the problem, not showcasing every possible approach.

### 4. Book cover image stored as a nullable URL string, not a file upload

**Why:** A cover image makes the portfolio demo look significantly better. Storing it as a simple `coverImageUrl` String field costs 5 minutes — it's just a database column and an `<img>` tag on the frontend. Actual file upload (multipart requests, disk/S3 storage, serving files) would cost 4–6 hours and introduces infrastructure complexity that's not relevant to a Library Management System's core purpose. The field is nullable because not every book entry needs an image — the frontend shows a placeholder if missing.

### 5. `copiesAvailable` as an int determines availability — no separate boolean field

**Why:** You can derive availability directly from the int — the boolean is redundant. Two fields that represent the same thing will get out of sync eventually and cause bugs. That's worse than the problem you're trying to solve.

---

## Technology Decisions

### 6. React + Vite for frontend (not Next.js)

**Why:** I have zero JavaScript experience. Next.js adds server-side rendering, file-based routing, and its own abstractions on top of React — three layers of new concepts at once. Plain React + Vite gives a fast setup where I only need to learn components, state, and API calls. The frontend is ~20% of this project; the backend is where interview value lives.

### 7. Spring Security + JWT for authentication (not BetterAuth)

**Why:** BetterAuth is a JavaScript/TypeScript library — it doesn't work with Java/Spring Boot. In the Java ecosystem, the industry standard is Spring Security with JWT tokens. This is what recruiters and interviewers expect to see on a Java backend project. Using the right tool for the stack matters.

### 8. PostgreSQL runs in Docker during development; backend and frontend do NOT

**Why:** During development, we want fast feedback loops. Running Spring Boot from IntelliJ gives hot reload and easy debugging. Running React via `npm run dev` gives hot reload. Only the database runs in a container because it's infrastructure, not application code. At the end of the project, we containerize everything for deployment — that's the production concern, not the dev concern. This mirrors how real development teams work.

### 9. Full Docker Compose setup is a deployment-phase task (Day 18)

**Why:** Writing Dockerfiles and docker-compose.yml for all services is done once the application is feature-complete. It proves we understand containerization without slowing down daily development with unnecessary build cycles.

### 10. CI/CD with GitHub Actions included (minimal pipeline)

**Why:** A basic pipeline (build + run tests on every push) takes ~1–2 hours to set up and sends a strong signal to recruiters that I understand modern development workflows. It's cheap for the value it adds. We keep it simple — no multi-stage deployments or complex workflows.

### 11. PostgreSQL running on port 5555

**Why:** Windows/Hyper-V reserves ports 5431–5530, which includes the default 5432 and 5433. Port 5555 is outside all excluded ranges.

### 12. Spring Boot running on port 8081 instead of 8080

**Why:** Port 8080 was already occupied by another process on the development machine. Port 8081 is used during development only.

---

## Architecture Decisions

### 13. Layered architecture: Controller → Service → Repository

**Why:** Standard Spring Boot pattern. Controllers handle HTTP (request/response mapping). Services handle business logic (borrow rules, validation). Repositories handle data access. Separation of concerns makes the code testable, maintainable, and easy to explain. This is what interviewers expect from a Java backend project.

### 14. Backend-focused project with minimal frontend

**Why:** I'm targeting backend / fullstack internships with a backend emphasis. The frontend needs to prove I can integrate with an API, handle authentication client-side, and build basic UI. It does NOT need to be beautiful. Time is better invested in solid backend logic, proper error handling, and deployment.

### 15. Service methods throw exceptions on "not found" — never return Optional

**Why:** Optional is appropriate when "not found" is a normal, expected outcome the caller should handle themselves. In a REST API, "not found" is an error — the correct response is 404, not 200 with an empty body. Returning Optional from a service method pushes the decision up to the controller, meaning every controller method has to handle it separately. Throwing an exception instead lets the GlobalExceptionHandler catch it once, in one place, and always return a consistent 404 response. Clean architecture means handling concerns at the right layer — "not found" is an exceptional case, so it belongs in exception handling.

### 16. Custom exception classes instead of RuntimeException

**Why:** Throwing a generic `RuntimeException` gives Spring no information about what went wrong — it defaults to 500 Internal Server Error regardless of the actual cause. Custom exceptions (`ResourceNotFoundException`, `BusinessException`) carry meaning. The `GlobalExceptionHandler` can catch each one specifically and map it to the correct HTTP status code (404, 400). This keeps error handling in one place and makes the API return honest, predictable responses to the client.

### 17. `existsById()` used in deleteBook instead of fetching the entity

**Why:** The only purpose of the lookup in `deleteBook` is to confirm the book exists before deleting it. Fetching the full entity with `findById()` just to get its id back is wasteful — we already have the id. `existsById()` hits the database once, returns a boolean, and avoids an unused variable. Use the simplest tool that solves the problem.

### 18. `@RequestMapping` at the class level in controllers

**Why:** Repeating the base path (`/api/v1/books`) on every method in the controller is unnecessary duplication. If the path ever changes, you'd have to update every method. Putting it at the class level with `@RequestMapping` means you only define it once — each method only declares what's unique about its path. This is standard Spring Boot practice.

### 19. `mapToDTO()` private helper method in BookService

**Why:** Without it, the same `new BookResponseDTO(...)` constructor call was repeated four times across `getAllBooks`, `getBookById`, `createBook`, and `updateBook`. If a field is ever added to `BookResponseDTO`, you'd have to find and update four separate places. Extracting it into a single private method means one change propagates everywhere. This is the DRY principle — Don't Repeat Yourself.

---

## Locked MVP Feature Set

- Register / Login (JWT)
- Role-based access: USER and ADMIN
- Book CRUD (Admin only)
- Public book browsing
- Borrow a book / Return a book
- Borrow status tracking (ACTIVE / RETURNED)
- Business rules: can't borrow already-borrowed book, max 3 active borrows per user
- Global exception handling + input validation
- Swagger API documentation
- Docker Compose for deployment
- Deployed to a free hosting platform (live URL)
- Basic CI/CD pipeline (GitHub Actions)
- README + this decision log

---

_Last updated: Day 4_