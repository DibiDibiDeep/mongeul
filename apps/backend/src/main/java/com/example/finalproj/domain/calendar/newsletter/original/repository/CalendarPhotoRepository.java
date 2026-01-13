package com.example.finalproj.domain.calendar.newsletter.original.repository;

import com.example.finalproj.domain.calendar.newsletter.original.entity.CalendarPhoto;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface CalendarPhotoRepository extends JpaRepository<CalendarPhoto, Integer> {
    // 특정 사용자 ID로 CalendarPhoto 목록을 조회
    List<CalendarPhoto> findByUserId(Integer userId);

    // 특정 아기 ID로 CalendarPhoto 목록을 조회
    List<CalendarPhoto> findByBabyId(Integer babyId);

    // 특정 날짜로 CalendarPhoto 목록을 조회
    List<CalendarPhoto> findByDate(LocalDateTime date);

    // 특정 날짜 범위로 CalendarPhoto 목록을 조회
    List<CalendarPhoto> findByDateBetween(LocalDateTime start, LocalDateTime end);

    // 특정 연도와 월로 CalendarPhoto 목록을 조회
    @Query("SELECT cp FROM CalendarPhoto cp WHERE YEAR(cp.date) = :year AND MONTH(cp.date) = :month")
    List<CalendarPhoto> findByYearAndMonth(@Param("year") int year, @Param("month") int month);
}
