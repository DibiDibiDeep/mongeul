package com.example.finalproj.domain.baby.original.repository;

import com.example.finalproj.domain.baby.original.entity.Baby;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BabyRepository extends JpaRepository<Baby, Integer> {
    // 사용자 ID로 아기 정보 찾기
    List<Baby> findByUserId(Integer userId);

    // 사용자 ID로 아기 정보 존재 여부 확인
    boolean existsByUserId(Integer userId);
}