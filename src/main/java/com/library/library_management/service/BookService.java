package com.library.library_management.service;

import com.library.library_management.dto.book.BookResponseDTO;
import com.library.library_management.repository.BookRepository;

import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class BookService {

    //Constructor injection necessary for modern springboot
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<BookResponseDTO> getAllBooks() {
        return bookRepository.findAll() // hits the database, returns List<Book>
                .stream() // opens a pipeline
                .map(book -> new BookResponseDTO( // transforms each Book
                        book.getId(),
                        book.getTitle(),
                        book.getAuthor(),
                        book.getIsbn(),
                        book.getPubYear(),
                        book.getCopiesAvailable(),
                        book.getCoverImageUrl(),
                        book.getCreatedAt()
                ))
                .toList(); // collects back into a List<BookResponseDTO>
    }

    public BookResponseDTO getBookById(Long id) {
        return bookRepository.findById(id)
                .map((book -> new BookResponseDTO( // transforms each Book
                        book.getId(),
                        book.getTitle(),
                        book.getAuthor(),
                        book.getIsbn(),
                        book.getPubYear(),
                        book.getCopiesAvailable(),
                        book.getCoverImageUrl(),
                        book.getCreatedAt()
                ))).orElseThrow(() -> new RuntimeException("Book not found with id: " + id));
    }
}
