# Library Management System

> ⚠️ **This project is currently in development.** Not all features are complete yet. I'm building this over 3 weeks as a portfolio project while learning Spring Boot.

---

## What is this?

A full-stack library management system where users can browse books, borrow and return them, and admins can manage the book catalogue. I'm building this to practice backend development with Java and Spring Boot, and to have something real to show in internship interviews.

The project covers things I wanted to get hands-on with: REST APIs, JWT authentication, role-based access control, database relationships, and eventually Docker deployment.

---

## Tech Stack

**Backend**
- Java 21
- Spring Boot 3.4.x
- PostgreSQL 17
- Spring Security + JWT
- Spring Data JPA / Hibernate

**Frontend** *(not started yet)*
- React + Vite

**Infrastructure** *(planned)*
- Docker Compose
- GitHub Actions CI/CD
- Render / Railway deployment

---

## Features

### Done
- [x] Book CRUD (create, read, update, delete)
- [x] Input validation and global error handling
- [x] Clean JSON error responses with status codes

### In Progress
- [ ] User registration and login
- [ ] JWT authentication
- [ ] Role-based authorization (USER vs ADMIN)

### Planned
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
- **Service** — all business logic and validation lives here
- **Repository** — talks to the database via Spring Data JPA
- **DTOs** — separate classes for what comes in and what goes out (entities are never exposed directly)

### Database Schema

```
users                 books                 borrows
─────                 ─────                 ───────
id                    id                    id
first_name            title                 user_id  → FK
last_name             author                book_id  → FK
email                 isbn                  borrow_date
password (hashed)     pub_year              due_date
role                  copies_available      return_date (nullable)
created_at            cover_image_url       status
                      created_at
```

### API Endpoints

| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| GET | `/api/v1/books` | Public | Get all books |
| GET | `/api/v1/books/{id}` | Public | Get book by ID |
| POST | `/api/v1/books` | Admin | Create a book |
| PUT | `/api/v1/books/{id}` | Admin | Update a book |
| DELETE | `/api/v1/books/{id}` | Admin | Delete a book |
| POST | `/api/auth/register` | Public | Register |
| POST | `/api/auth/login` | Public | Login |
| POST | `/api/v1/borrows/{bookId}/borrow` | User | Borrow a book |
| PUT | `/api/v1/borrows/{borrowId}/return` | User | Return a book |
| GET | `/api/v1/borrows/my` | User | My borrow history |
| GET | `/api/v1/borrows` | Admin | All borrows |

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

3. Run the backend
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
│       ├── config/            # Security config
│       ├── controller/        # REST controllers
│       ├── dto/               # Data transfer objects
│       ├── exception/         # Custom exceptions + global handler
│       ├── model/             # JPA entities
│       ├── repository/        # Spring Data repositories
│       └── service/           # Business logic
├── library-frontend/          # React frontend (coming soon)
├── docker-compose.yml
├── decisions.md               # Why I made certain technical choices
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

## Notes

I'm keeping a `decisions.md` file in the root of the project where I document why I made certain choices (like why pessimistic locking over optimistic, why constructor injection, etc.). Useful for interview prep.

---

*Built by [Abdottawab KERAOUI] — currently learning Spring Boot and backend development.*