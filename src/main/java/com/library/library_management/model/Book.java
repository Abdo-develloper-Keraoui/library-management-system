package com.library.library_management.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/*
*  @Entity, @Id, @GeneratedValue, @Column
*
* */
@Entity
@Table(name = "book")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Specifies primary key generation strategy
    private Long id;

    @Column(name = "title", nullable = false, length = 50)
    private String title;

    @Column(name = "author", nullable = false, length = 50)
    private String author;

    @Column(name = "isbn", nullable = false, length = 50)
    private String isbn;

    @Column(name = "pub_year", nullable = false)
    private int pubYear;

    @Column(name = "copies_available", nullable = false)
    private int copiesAvailable;

    @Column(name = "cover_image_url")
    private String coverImageUrl;

    //date of creation of book inside my app
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // Default constructor is required by JPA
    protected Book() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public int getPubYear() {
        return pubYear;
    }

    public void setPubYear(int pubYear) {
        this.pubYear = pubYear;
    }

    public int getCopiesAvailable() {
        return copiesAvailable;
    }

    public void setCopiesAvailable(int copiesAvailable) {
        this.copiesAvailable = copiesAvailable;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}