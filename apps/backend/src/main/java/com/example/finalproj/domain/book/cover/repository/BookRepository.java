package com.example.finalproj.domain.book.cover.repository;

import com.example.finalproj.domain.book.cover.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {
    // ID로 책을 조회하면서 관련된 페이지도 함께 가져오는 쿼리
    @Query("SELECT b FROM Book b LEFT JOIN FETCH b.pages WHERE b.bookId = :id")
    Optional<Book> findByIdWithPages(@Param("id") Integer id);

    // userId로 책을 조회하는 메서드 추가
    List<Book> findByUserId(Integer userId);

    // userId와 babyId로 책을 조회하는 메서드
    List<Book> findByUserIdAndBabyId(Integer userId, Integer babyId);

    List<Book> findByBabyId(Integer babyId);
}