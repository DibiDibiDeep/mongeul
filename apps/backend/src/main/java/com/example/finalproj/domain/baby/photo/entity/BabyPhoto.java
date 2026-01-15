package com.example.finalproj.domain.baby.photo.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "baby_photos")
public class BabyPhoto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "baby_photo_id")
    private Integer babyPhotoId;

    @Column(name = "baby_id", nullable = false)
    private Integer babyId;

    @Column(name = "user_id", nullable = false)  // 추가된 필드
    private Integer userId;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Column(name = "upload_date", nullable = false)
    private LocalDateTime uploadDate;

    // 모든 필드를 포함한 생성자
    public BabyPhoto(Integer babyPhotoId, Integer babyId, Integer userId, String filePath, LocalDateTime uploadDate) {
        this.babyPhotoId = babyPhotoId;
        this.babyId = babyId;
        this.userId = userId;
        this.filePath = filePath;
        this.uploadDate = uploadDate;
    }

    // 기본 생성자
    public BabyPhoto() {
    }

    // Getter와 Setter 메서드
    public Integer getBabyPhotoId() {
        return babyPhotoId;
    }

    public void setBabyPhotoId(Integer babyPhotoId) {
        this.babyPhotoId = babyPhotoId;
    }

    public Integer getBabyId() {
        return babyId;
    }

    public void setBabyId(Integer babyId) {
        this.babyId = babyId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public LocalDateTime getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(LocalDateTime uploadDate) {
        this.uploadDate = uploadDate;
    }

    // toString 메서드 오버라이드
    @Override
    public String toString() {
        return "BabyPhoto{" +
                "babyPhotoId=" + babyPhotoId +
                ", babyId=" + babyId +
                ", userId=" + userId +
                ", filePath='" + filePath + '\'' +
                ", uploadDate=" + uploadDate +
                '}';
    }
}