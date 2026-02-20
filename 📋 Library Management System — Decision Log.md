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

---

## Technology Decisions

### 4. React + Vite for frontend (not Next.js)

**Why:** I have zero JavaScript experience. Next.js adds server-side rendering, file-based routing, and its own abstractions on top of React — three layers of new concepts at once. Plain React + Vite gives a fast setup where I only need to learn components, state, and API calls. The frontend is ~20% of this project; the backend is where interview value lives.

### 5. Spring Security + JWT for authentication (not BetterAuth)

**Why:** BetterAuth is a JavaScript/TypeScript library — it doesn't work with Java/Spring Boot. In the Java ecosystem, the industry standard is Spring Security with JWT tokens. This is what recruiters and interviewers expect to see on a Java backend project. Using the right tool for the stack matters.

### 6. PostgreSQL runs in Docker during development; backend and frontend do NOT

**Why:** During development, we want fast feedback loops. Running Spring Boot from IntelliJ gives hot reload and easy debugging. Running React via `npm run dev` gives hot reload. Only the database runs in a container because it's infrastructure, not application code. At the end of the project, we containerize everything for deployment — that's the production concern, not the dev concern. This mirrors how real development teams work.

### 7. Full Docker Compose setup is a deployment-phase task (Day 18)

**Why:** Writing Dockerfiles and docker-compose.yml for all services is done once the application is feature-complete. It proves we understand containerization without slowing down daily development with unnecessary build cycles.

### 8. CI/CD with GitHub Actions included (minimal pipeline)

**Why:** A basic pipeline (build + run tests on every push) takes ~1–2 hours to set up and sends a strong signal to recruiters that I understand modern development workflows. It's cheap for the value it adds. We keep it simple — no multi-stage deployments or complex workflows.

### 9. Postgres is Running on port 5555 that falls outside excluded range 
PostgreSQL runs on host port 5555 (mapped to container port 5432) because Windows/Hyper-V reserves ports 5431–5530, which includes the default 5432 and 5433. Port 5555 is outside all excluded ranges.

### 10.Changed Spring Boot port from 8080 to 8081
now http://localhost:8081/api/v1/books it looks something like this instead of port 8080 the automatic
**Why:** Port 8080 was already occupied by another process on the development machine. Port 8081 is used during development only.

---

## Architecture Decisions

### 11. Layered architecture: Controller → Service → Repository

**Why:** Standard Spring Boot pattern. Controllers handle HTTP (request/response mapping). Services handle business logic (borrow rules, validation). Repositories handle data access. Separation of concerns makes the code testable, maintainable, and easy to explain. This is what interviewers expect from a Java backend project.

### 12. Backend-focused project with minimal frontend

**Why:** I'm targeting backend / fullstack internships with a backend emphasis. The frontend needs to prove I can integrate with an API, handle authentication client-side, and build basic UI. It does NOT need to be beautiful. Time is better invested in solid backend logic, proper error handling, and deployment.

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

_Last updated: Day 2