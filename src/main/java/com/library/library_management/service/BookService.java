package com.library.library_management.service;

import com.library.library_management.dto.book.BookCreateDTO;
import com.library.library_management.dto.book.BookResponseDTO;
import com.library.library_management.dto.book.BookUpdateDTO;
import com.library.library_management.model.Book;
import com.library.library_management.repository.BookRepository;

import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.List;


@Service
public class BookService {

    //Constructor injection necessary for modern springboot
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    //TODO private BookResponseDTO mapToDTO(Book book) { ... } make this helper method
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

    private void validatePubYear(Integer pubYear) {
        if (pubYear != null && pubYear > Year.now().getValue()) {
            //TODO make this exception a business exceptions
            throw new RuntimeException("Publication year cannot be in the future");
        }
    }

    public BookResponseDTO createBook(BookCreateDTO dto) {
        validatePubYear(dto.getPubYear());
        Book book = new Book();
        book.setTitle(dto.getTitle());
        book.setAuthor(dto.getAuthor());
        book.setIsbn(dto.getIsbn());
        book.setPubYear(dto.getPubYear().intValue());
        book.setCopiesAvailable(dto.getCopiesAvailable());
        book.setCoverImageUrl(dto.getCoverImageUrl());

        Book savedBook = bookRepository.save(book);
        BookResponseDTO bookResonseDTO = new BookResponseDTO( // transforms each Book
                savedBook.getId(),
                savedBook.getTitle(),
                savedBook.getAuthor(),
                savedBook.getIsbn(),
                savedBook.getPubYear(),
                savedBook.getCopiesAvailable(),
                savedBook.getCoverImageUrl(),
                savedBook.getCreatedAt()
        );

        return bookResonseDTO;
    }


    public BookResponseDTO updateBook(Long id, BookUpdateDTO dto) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));

        if (dto.getTitle() != null) {
            book.setTitle(dto.getTitle());
        }
        if (dto.getAuthor() != null) {
            book.setAuthor(dto.getAuthor());
        }
        if (dto.getIsbn() != null) {
            book.setIsbn(dto.getIsbn());
        }
        if (dto.getPubYear() != null) {
            validatePubYear(dto.getPubYear());
            book.setPubYear(dto.getPubYear());
        }
        if (dto.getCopiesAvailable() != null) {
            book.setCopiesAvailable(dto.getCopiesAvailable());
        }
        if (dto.getCoverImageUrl() != null) {
            book.setCoverImageUrl(dto.getCoverImageUrl());
        }

        Book updatedBook = bookRepository.save(book);

        return new BookResponseDTO(
                updatedBook.getId(),
                updatedBook.getTitle(),
                updatedBook.getAuthor(),
                updatedBook.getIsbn(),
                updatedBook.getPubYear(),
                updatedBook.getCopiesAvailable(),
                updatedBook.getCoverImageUrl(),
                updatedBook.getCreatedAt()
        );
    }
    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));
        bookRepository.deleteById(book.getId());
    }

}
