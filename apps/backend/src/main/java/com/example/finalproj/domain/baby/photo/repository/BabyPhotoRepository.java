package com.example.finalproj.domain.baby.photo.repository;

import com.example.finalproj.domain.baby.photo.entity.BabyPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BabyPhotoRepository extends JpaRepository<BabyPhoto, Integer> {
    // 특정 아기의 모든 사진 찾기
    List<BabyPhoto> findByBabyId(Integer babyId);

    // 특정 날짜에 업로드된 사진 찾기
    List<BabyPhoto> findByUploadDate(LocalDateTime uploadDate);

    Optional<Object> findTopByBabyIdOrderByUploadDateDesc(Integer babyId);

    List<BabyPhoto> findByUserIdAndBabyId(Integer userId, Integer babyId);

//    BabyPhoto edit(BabyPhoto babyPhoto);
}