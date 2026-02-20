package com.library.library_management.repository;

import com.library.library_management.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
    // JpaRepository provides methods like save(), findById(), findAll(), delete(), etc.
    /**Given by JpaRepository
     * findAll()                  → JpaRepository gives you this free
     * findById(id)               → JpaRepository gives you this free
     * save(book)                 → JpaRepository gives you this free (insert + update)
     * deleteById(id)             → JpaRepository gives you this free
     * existsById(id)             → JpaRepository gives you this free
     */
}
