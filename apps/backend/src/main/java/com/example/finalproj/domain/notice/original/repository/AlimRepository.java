package com.example.finalproj.domain.notice.original.repository;

import com.example.finalproj.domain.baby.photo.entity.BabyPhoto;
import com.example.finalproj.domain.notice.original.entity.Alim;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AlimRepository extends JpaRepository<Alim, Integer> {

    // 주어진 날짜에 해당하는 Alim 레코드를 조회
    @Query("SELECT a FROM Alim a WHERE DATE(a.date) = :date")
    List<Alim> findByDate(@Param("date") LocalDate date);

    // 주어진 사용자 ID와 날짜 범위 내의 Alim 레코드를 조회
    List<Alim> findByUserIdAndDateBetween(Integer userId, LocalDateTime start, LocalDateTime end);

    List<Alim> findByBabyId(Integer babyId);

    List<Alim> findByUserIdAndBabyId(Integer userId, Integer babyId);
}
