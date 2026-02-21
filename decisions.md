# 🔄 Library Management System — Session Recap & Continuation Prompt

> Paste this entire document at the start of a new conversation with Claude to continue where we left off.

---

## 🎯 Project Overview

Building a **Library Management System** as a 3-week portfolio project for fullstack (backend-focused) internship applications. Claude is acting as a **strict senior software engineer mentor** — not building the project, but guiding step by step.

**Level:** Basic Java/OOP, basic SQL, zero frontend experience, no Spring Boot/Docker/deployment experience. ~70 total hours over 20 days.

---

## ✅ Locked MVP Scope

- **Backend:** Java 21, Spring Boot 4.0.x, PostgreSQL 17, JWT authentication, role-based authorization (USER, ADMIN)
- **Entities:** User, Book (author is a String field), Borrow
- **Book has `coverImageUrl`** — nullable String, no file upload
- **Borrow statuses:** ACTIVE, RETURNED only
- **Features:** Register, Login, Browse books (public), Borrow/Return (authenticated), Admin Book CRUD, Borrow history
- **Business rules:** Can't borrow already-borrowed book, max 3 active borrows per user
- **Concurrency:** Pessimistic locking (SELECT ... FOR UPDATE)
- **Frontend:** React + Vite, minimal UI, JWT stored client-side
- **Deployment:** Docker Compose, deployed to Render/Railway
- **Extras:** Swagger, GitHub Actions CI/CD, README, decisions.md

---

## 🗺️ 3-Week Roadmap

### WEEK 1 — Backend Foundation (Days 1–7) ✅ COMPLETE
| Day | Focus | Status |
|-----|-------|--------|
| 1–2 | Project setup, PostgreSQL in Docker, connect Spring Boot, first GET endpoint | ✅ |
| 3–4 | Book entity + full CRUD, layered architecture, validation, global error handler | ✅ |
| 5–6 | User entity, registration, BCrypt password hashing | ✅ |
| 7 | JWT authentication — filter, login endpoint, SecurityConfig | ✅ |

### WEEK 2 — Core Logic + Frontend Start (Days 8–14)
| Day | Focus | Status |
|-----|-------|--------|
| 8–9 | Role-based authorization (ADMIN vs USER) | ✅ COMPLETE |
| 10–11 | Borrow entity, borrow/return endpoints, business rules, pessimistic locking | ← **START HERE** |
| 12 | Borrow history, Swagger. Backend feature-complete. | 📅 |
| 13–14 | React + Vite setup, login page | 📅 |

### WEEK 3 — Frontend + Polish + Deploy (Days 15–20)
| Day | Focus |
|-----|-------|
| 15–16 | Book browsing, borrow/return buttons, protected routes |
| 17 | Admin page |
| 18 | Docker Compose + GitHub Actions |
| 19 | Deploy to Render/Railway |
| 20 | README, cleanup, practice explaining |

---

## 🏗️ What's Already Done

- [x] Docker Desktop installed (WSL2)
- [x] PostgreSQL 17 running in Docker (`library-db`, port **5555**)
- [x] Spring Boot project on port **8081**
- [x] Database connection confirmed
- [x] `Book` entity, `BookRepository`, `BookResponseDTO`, `BookService`, `BookController` — all 5 endpoints complete
- [x] `BookCreateDTO` and `BookUpdateDTO` with validation
- [x] `ResourceNotFoundException`, `BusinessException`, `ErrorResponse`, `GlobalExceptionHandler`
- [x] All 6 Postman tests passed for Book endpoints
- [x] DBeaver connected to PostgreSQL
- [x] Git repo created and pushed to GitHub (public)
- [x] README.md written
- [x] decisions.md up to date
- [x] `Role.java` enum (USER, ADMIN)
- [x] `User.java` entity
- [x] `UserRepository` with `findByEmail()` and `existsByEmail()`
- [x] `RegisterDTO`, `LoginDTO`, `AuthResponseDTO`
- [x] `AuthService` with `register()` and `login()` methods
- [x] `AuthController` with `POST /api/v1/auth/register` and `POST /api/v1/auth/login`
- [x] All Postman tests passed for auth endpoints
- [x] JWT dependency added to `pom.xml` (jjwt 0.12.6 — api, impl, jackson)
- [x] `jwt.secret` and `jwt.expiration` in `application.properties` as `${JWT_SECRET}` and `${JWT_EXPIRATION:86400000}`
- [x] `JWT_SECRET` set as environment variable in IntelliJ run configuration (NOT hardcoded)
- [x] `JwtUtils.java` — generates, validates, extracts email from token ✅
- [x] `CustomUserDetailsService.java` — loads user by email, wraps in Spring Security `User` object ✅
- [x] `JwtAuthenticationFilter.java` — complete with blank token check + UsernameNotFoundException catch ✅
- [x] `SecurityConfig.java` — stateless sessions, JWT filter registered, public vs protected endpoints defined, authenticationEntryPoint configured, /error permitted ✅
- [x] Full JWT flow tested in Postman — register → login → token returned ✅
- [x] `@EnableMethodSecurity` added to `SecurityConfig` ✅
- [x] `@PreAuthorize("hasRole('ADMIN')")` added to `createBook`, `updateBook`, `deleteBook` in `BookController` ✅
- [x] Role-based authorization fully tested in Postman — 401 (no token), 403 (USER token), 201 (ADMIN token) ✅
- [x] Two test users in DB: `user@test.com` (USER) and `admin@test.com` (ADMIN)

---

## 📁 Project Folder Structure

```
C:/Projects/library-system/
├── library-management/
│   └── src/main/java/com/library/library_management/
│       ├── LibraryManagementApplication.java
│       ├── config/
│       │   └── SecurityConfig.java
│       ├── controller/
│       │   ├── BookController.java
│       │   └── AuthController.java
│       ├── dto/
│       │   ├── auth/
│       │   │   ├── RegisterDTO.java
│       │   │   ├── LoginDTO.java
│       │   │   └── AuthResponseDTO.java
│       │   ├── book/
│       │   │   ├── BookCreateDTO.java
│       │   │   ├── BookUpdateDTO.java
│       │   │   └── BookResponseDTO.java
│       │   └── ErrorResponse.java
│       ├── exception/
│       │   ├── ResourceNotFoundException.java
│       │   ├── BusinessException.java
│       │   └── GlobalExceptionHandler.java
│       ├── model/
│       │   ├── Book.java
│       │   ├── User.java
│       │   └── Role.java
│       ├── repository/
│       │   ├── BookRepository.java
│       │   └── UserRepository.java
│       ├── security/
│       │   ├── JwtUtils.java
│       │   ├── CustomUserDetailsService.java
│       │   └── JwtAuthenticationFilter.java
│       └── service/
│           ├── BookService.java
│           └── AuthService.java
├── library-frontend/
├── decisions.md
├── docker-compose.yml
└── README.md
```

---

## 📄 Current File Contents

### `SecurityConfig.java`
```java
package com.library.library_management.config;

import com.library.library_management.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) ->
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized"))
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/books/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

### `BookController.java`
```java
package com.library.library_management.controller;

import com.library.library_management.dto.book.BookCreateDTO;
import com.library.library_management.dto.book.BookResponseDTO;
import com.library.library_management.dto.book.BookUpdateDTO;
import com.library.library_management.service.BookService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public ResponseEntity<List<BookResponseDTO>> getBooks() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookResponseDTO> getBookById(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<BookResponseDTO> createBook(@RequestBody @Valid BookCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookService.createBook(dto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<BookResponseDTO> updateBook(@PathVariable Long id, @RequestBody @Valid BookUpdateDTO dto) {
        return ResponseEntity.ok(bookService.updateBook(id, dto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
}
```

### `JwtUtils.java`
```java
package com.library.library_management.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public String generateToken(String email) {
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    public String extractEmail(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
```

### `CustomUserDetailsService.java`
```java
package com.library.library_management.security;

import com.library.library_management.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .map(user -> org.springframework.security.core.userdetails.User
                        .withUsername(user.getEmail())
                        .password(user.getPassword())
                        .roles(user.getRole().name())
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }
}
```

### `JwtAuthenticationFilter.java`
```java
package com.library.library_management.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtUtils jwtUtils;

    public JwtAuthenticationFilter(CustomUserDetailsService customUserDetailsService, JwtUtils jwtUtils) {
        this.customUserDetailsService = customUserDetailsService;
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        if (token.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        if (jwtUtils.validateToken(token)) {
            String email = jwtUtils.extractEmail(token);
            try {
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } catch (UsernameNotFoundException e) {
                // Token valid but user deleted — skip auth, SecurityConfig returns 401
            }
        }

        filterChain.doFilter(request, response);
    }
}
```

### `application.properties` (relevant lines)
```properties
jwt.secret=${JWT_SECRET}
jwt.expiration=${JWT_EXPIRATION:86400000}
```

---

## ▶️ WHERE WE LEFT OFF — Next Task

**Day 10: Borrow entity, borrow/return endpoints, business rules, pessimistic locking**

Day 8 is fully complete. Role-based authorization works end to end:
- No token → 401
- USER token on admin endpoint → 403
- ADMIN token on admin endpoint → 201

Next up is the core business logic — the borrow system. This involves:
- `Borrow.java` entity with `@ManyToOne` relationships to User and Book
- `BorrowStatus.java` enum (ACTIVE, RETURNED)
- `BorrowRepository` with custom queries
- `BorrowService` with all business rules
- `BorrowController` with 4 endpoints
- Pessimistic locking on the book row during borrow

---

## 🧠 Concepts Learned So Far

| Concept | What it means |
|---|---|
| `Optional<T>` | Wrapper for "might not exist" — forces handling both cases |
| `.orElseThrow()` | Unwraps Optional or throws exception |
| `@PrePersist` | Runs automatically before Hibernate inserts |
| `@NotBlank` vs `@NotNull` | `@NotBlank` for Strings, `@NotNull` for objects |
| Custom exceptions | Maps to correct HTTP status, caught by GlobalExceptionHandler |
| `@RestControllerAdvice` | Watches all controllers for exceptions, handles centrally |
| `existsById()` vs `findById()` | Use existsById when you only need to check existence |
| `@Enumerated(EnumType.STRING)` | Stores enum as string in DB |
| BCrypt hashing in service layer | Entity stores plain String; service hashes before saving |
| `MethodArgumentNotValidException` | Thrown by `@Valid` — caught in GlobalExceptionHandler |
| JWT structure | Header.Payload.Signature — payload is encoded not encrypted |
| JWT signature | Signed with secret key — tampered payload breaks signature |
| Secret key in environment variable | Never hardcode — anyone with the key can forge tokens |
| `@Value("${property}")` | Reads value from application.properties into a Java field |
| `jjwt-impl` and `jjwt-jackson` are runtime scope | Don't import their classes directly — only `jjwt-api` is compile scope |
| `getSigningKey()` extracted as helper | DRY — three methods need it, extract once |
| `.subject(email)` in JWT | Stores email in the payload — retrieved with `.getSubject()` |
| `validateToken()` uses try/catch | jjwt throws exceptions on invalid tokens, not returns false |
| `UserDetailsService` interface | Spring Security contract — implement it to tell Spring how to load a user |
| Spring Security `User` builder | Wraps your entity into a `UserDetails` object Spring understands |
| `OncePerRequestFilter` | Base class for filters that run exactly once per HTTP request |
| `@Component` on filter | Registers as a Spring bean — infrastructure, not business logic |
| Filter vs authorization | Filter = authentication (who are you?), SecurityConfig = authorization (what can you do?) |
| `SecurityContextHolder` | Spring Security's per-request memory — filter writes to it, SecurityConfig reads from it |
| `UsernamePasswordAuthenticationToken` | Spring Security's auth object — wraps user + roles, placed in SecurityContext |
| `SessionCreationPolicy.STATELESS` | No HTTP sessions — JWT IS the session |
| `addFilterBefore(...)` | Registers our JWT filter to run before Spring's default auth filter |
| Authentication vs Authorization | Authentication = who are you (filter). Authorization = what can you do (SecurityConfig). |
| Same error message for wrong email/password | Security — don't reveal which emails are registered |
| `passwordEncoder.matches(raw, encoded)` | BCrypt comparison — never compare plain strings to hashes |
| `@EnableMethodSecurity` | Enables `@PreAuthorize` annotations — without it they are silently ignored |
| `@PreAuthorize("hasRole('ADMIN')")` | Enforces role check at the method level — evaluated before the method runs |
| SecurityConfig broad rules + @PreAuthorize fine-grained rules | Industry standard — SecurityConfig handles public vs authenticated, @PreAuthorize handles role-specific |
| 401 vs 403 | 401 = "I don't know who you are" (no/invalid token). 403 = "I know who you are but you're not allowed" (wrong role) |
| `authenticationEntryPoint` | Tells Spring what to do when an unauthenticated request hits a protected endpoint — returns 401 |
| `/error` must be permitted | Spring internally forwards to `/error` when returning error responses — if it's protected, 401 bleeds through and overwrites the real status code |

---

## 🧑‍🏫 Mentor Behaviour Rules

Claude is acting as a strict senior engineer mentor:
- Does NOT build the project — guides to do it independently
- Asks probing questions before revealing answers
- Challenges weak thinking, forces justification of choices
- Teaches concepts just-in-time
- Gives small code snippets with explanations only when needed
- Keeps on track for 3-week deadline
- Occasionally asks "why did you choose X over Y?" for interview prep

---

*Session ended: Day 8 complete. Resume from: Day 10 — Borrow entity and business logic.*