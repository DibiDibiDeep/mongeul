package com.example.finalproj.domain.memo.original.repository;

import com.example.finalproj.domain.memo.original.entity.Memo;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface MemoRepository extends JpaRepository<Memo, Integer> {

    // 특정 날짜의 메모 조회
    @Query("SELECT m FROM Memo m WHERE DATE(m.date) = :date")
    List<Memo> findByDate(@Param("date") LocalDate date);

    // 날짜 범위 내의 메모 조회
    @Query("SELECT m FROM Memo m WHERE m.date BETWEEN :startDate AND :endDate")
    List<Memo> findByDateBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // 특정 사용자의 날짜 범위 내 메모 조회
    @Query("SELECT m FROM Memo m WHERE m.userId = :userId AND m.date BETWEEN :startDate AND :endDate")
    List<Memo> findByUserIdAndDateBetween(
            @Param("userId") Integer userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    List<Memo> findByUserIdAndBabyId(Integer userId, Integer babyId);

    List<Memo> findByBabyId(Integer babyId);
}