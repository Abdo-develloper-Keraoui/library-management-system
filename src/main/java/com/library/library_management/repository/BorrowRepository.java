package com.library.library_management.repository;

import com.library.library_management.model.Borrow;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BorrowRepository extends JpaRepository<Borrow, Long> {

    //here we get some sql queries for free


}
