package com.example.finalproj.domain.chat.diary.repository;

import com.example.finalproj.domain.chat.diary.entity.TodaySum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TodaySumRepository extends JpaRepository<TodaySum, Integer> {

    // 특정 사용자와 아기에 대한 TodaySum 조회
    List<TodaySum> findByUserIdAndBabyId(Integer userId, Integer babyId);

    List<TodaySum> findByBabyId(Integer babyId);

    List<TodaySum> getTodaySumByUserId(Integer userId);
}
