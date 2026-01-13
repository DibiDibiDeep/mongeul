package com.example.finalproj.domain.baby.original.entity;

import com.example.finalproj.domain.baby.photo.entity.BabyPhoto;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Baby")
public class Baby {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "baby_id")
    private Integer babyId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "baby_name", nullable = false)
    private String babyName;

    @Column(nullable = false)
    private LocalDateTime birth;

    @Column(nullable = false, length = 10)
    private String gender;

    // 기본 생성자
    public Baby() {}

    // 모든 필드를 포함한 생성자
    public Baby(Integer babyId, Integer userId, String babyName, LocalDateTime birth, String gender) {
        this.babyId = babyId;
        this.userId = userId;
        this.babyName = babyName;
        this.birth = birth;
        this.gender = gender;
    }

    // Getter와 Setter 메서드
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

    public String getBabyName() {
        return babyName;
    }

    public void setBabyName(String babyName) {
        this.babyName = babyName;
    }

    public LocalDateTime getBirth() {
        return birth;
    }

    public void setBirth(LocalDateTime birth) {
        this.birth = birth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    // toString 메서드 오버라이드
    @Override
    public String toString() {
        return "Baby{" +
                "babyId=" + babyId +
                ", userId=" + userId +
                ", babyName='" + babyName + '\'' +
                ", birth=" + birth +
                ", gender='" + gender + '\'' +
                '}';
    }

}