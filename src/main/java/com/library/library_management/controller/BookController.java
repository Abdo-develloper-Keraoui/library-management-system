package com.library.library_management.controller;

import com.library.library_management.dto.book.BookResponseDTO;
import com.library.library_management.service.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) { //instructor injection
        this.bookService = bookService;
    }

    /**
     * Retrieves all books.
     * GET /api/v1/books → returns 200 OK with a list of BookResponseDTO.
     */
    @GetMapping("/api/v1/books")
    public ResponseEntity<List<BookResponseDTO>> getBooks() {
        List<BookResponseDTO> books = bookService.getAllBooks();
        return ResponseEntity.ok(books);
    }

    @GetMapping("/api/v1/books/{id}")
    public ResponseEntity<BookResponseDTO> getBookById(@PathVariable Long id) {
        BookResponseDTO book = bookService.getBookById(id);
        return ResponseEntity.ok(book);
    }
}
