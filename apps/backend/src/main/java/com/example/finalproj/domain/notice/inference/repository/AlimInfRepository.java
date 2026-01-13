package com.example.finalproj.domain.notice.inference.repository;

import com.example.finalproj.domain.baby.photo.entity.BabyPhoto;
import com.example.finalproj.domain.notice.inference.entity.AlimInf;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AlimInfRepository extends JpaRepository<AlimInf, Integer> {

    // 정해진 기간 내 AlimInf 조회
    @Query("SELECT a FROM AlimInf a WHERE a.date BETWEEN :startDate AND :endDate")
    List<AlimInf> findByDateBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // alimId에 해당하는 모든 AlimInf 조회
    List<AlimInf> findByAlimId(Integer alimId);

    // alimId에 해당하는 가장 최근의 AlimInf 조회
    @Query("SELECT a FROM AlimInf a WHERE a.alimId = :alimId ORDER BY a.date DESC")
    List<AlimInf> findLatestByAlimId(@Param("alimId") Integer alimId);

    // alimId의 최대 값을 가져오는 쿼리
    @Query("SELECT COALESCE(MAX(a.alimId), 0) FROM AlimInf a")
    Integer findMaxAlimId();

    // alimId에 해당하는 AlimInf 삭제
    void deleteByAlimId(Integer alimId);

    // 특정 아기의 Alim 찾기
    List<AlimInf> findByBabyId(Integer babyId);
}