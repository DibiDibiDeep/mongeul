package com.example.finalproj.domain.baby.photo.controller;

import com.example.finalproj.domain.baby.photo.entity.BabyPhoto;
import com.example.finalproj.domain.baby.photo.service.BabyPhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/baby-photos")
public class BabyPhotoController {

    @Autowired
    private BabyPhotoService babyPhotoService;

    // 모든 아기 사진 조회
    @GetMapping
    public ResponseEntity<List<BabyPhoto>> getAllBabyPhotos() {
        return ResponseEntity.ok(babyPhotoService.getAllBabyPhotos());
    }

    // 특정 아기 사진 조회
    @GetMapping("/{id}")
    public ResponseEntity<BabyPhoto> getBabyPhotoById(@PathVariable Integer id) {
        return babyPhotoService.getBabyPhotoById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 특정 아기의 모든 사진 조회
    @GetMapping("/baby/{babyId}")
    public ResponseEntity<List<BabyPhoto>> getBabyPhotosByBabyId(@PathVariable Integer babyId) {
        return ResponseEntity.ok(babyPhotoService.getBabyPhotosByBabyId(babyId));
    }

    // 특정 날짜에 업로드된 사진 조회
    @GetMapping("/date/{date}")
    public ResponseEntity<List<BabyPhoto>> getBabyPhotosByUploadDate(@PathVariable LocalDateTime date) {
        return ResponseEntity.ok(babyPhotoService.getBabyPhotosByUploadDate(date));
    }

    // 새로운 아기 사진 업로드
    @PutMapping("/{id}")
    public ResponseEntity<?> updateBabyPhoto(
            @PathVariable Integer id,
            @RequestParam("file") MultipartFile file,
            @RequestParam("babyId") Integer babyId,
            @RequestParam(value = "userId", required = false) Integer userId) {
        try {
            String contentType = file.getContentType();
            if (!"image/jpeg".equals(contentType) && !"image/png".equals(contentType)) {
                return ResponseEntity.badRequest().body("Only JPG and PNG files are allowed.");
            }

            BabyPhoto updatedOrCreatedPhoto = babyPhotoService.createOrUpdateBabyPhoto(file, babyId, userId);
            return ResponseEntity.ok(updatedOrCreatedPhoto);

        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Failed to update file: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Unexpected error: " + e.getMessage());
        }
    }

    @GetMapping("/user/{userId}/baby/{babyId}")
    public ResponseEntity<List<BabyPhoto>> getBabyPhotosByUserIdAndBabyId(
            @PathVariable Integer userId,
            @PathVariable Integer babyId) {
        return ResponseEntity.ok(babyPhotoService.getBabyPhotosByUserIdAndBabyId(userId, babyId));
    }

    // 아기 사진 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBabyPhoto(@PathVariable Integer id) {
        babyPhotoService.deleteBabyPhoto(id);
        return ResponseEntity.ok().build();
    }
}