package com.example.finalproj.domain.calendar.schedule.repository;

import com.example.finalproj.domain.calendar.schedule.entity.Calendar;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface CalendarRepository extends JpaRepository<Calendar, Integer> {
    // 특정 사용자 ID로 Calendar 목록을 조회
    List<Calendar> findByUserId(Integer userId);

    // 특정 아기 ID로 Calendar 목록을 조회
    List<Calendar> findByBabyId(Integer babyId);

    // 특정 날짜로 시작 시간을 기준으로 Calendar 목록을 조회
    @Query("SELECT c FROM Calendar c WHERE FUNCTION('DATE', c.startTime) = :date")
    List<Calendar> findByStartTimeDate(@Param("date") LocalDate date);

    // 특정 날짜 범위로 시작 시간을 기준으로 Calendar 목록을 조회
    @Query("SELECT c FROM Calendar c WHERE c.startTime BETWEEN :startDate AND :endDate")
    List<Calendar> findByStartTimeBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // 특정 사용자 ID와 시작 시간, 제목이 존재하는지 확인
    boolean existsByUserIdAndBabyIdAndStartTimeAndTitle(Integer userId, Integer babyId, LocalDateTime startTime, String title);

    List<Calendar> findByUserIdAndBabyId(Integer userId, Integer babyId);
}
