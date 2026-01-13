package com.example.finalproj.domain.calendar.newsletter.original.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "Calendar_Photo")
public class CalendarPhoto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer calendarPhotoId;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "baby_id")
    private Integer babyId;

    private String filePath;

    private LocalDateTime date;

    public CalendarPhoto(Integer calendarPhotoId, Integer userId, Integer babyId, String filePath, LocalDateTime date) {
        this.calendarPhotoId = calendarPhotoId;
        this.userId = userId;
        this.babyId = babyId;
        this.filePath = filePath;
        this.date = date;
    }

    public CalendarPhoto() {

    }

    public Integer getCalendarPhotoId() {
        return calendarPhotoId;
    }

    public void setCalendarPhotoId(Integer calendarPhotoId) {
        this.calendarPhotoId = calendarPhotoId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getBabyId() {
        return babyId;
    }

    public void setBabyId(Integer babyId) {
        this.babyId = babyId;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "CalendarPhoto{" +
                "calendarPhotoId=" + calendarPhotoId +
                ", userId=" + userId +
                ", babyId=" + babyId +
                ", filePath='" + filePath + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}