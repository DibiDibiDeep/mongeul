package com.example.finalproj.domain.calendar.newsletter.inference.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "calendar_photo_infs")
public class CalendarPhotoInf {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "calendar_photo_inf_id")
    private Integer calendarPhotoInfId;

    @Column(name = "calendar_photo_id")
    private Integer calendarPhotoId;

    @Column(name = "inference_result", columnDefinition = "TEXT")
    private String inferenceResult;

    @Column(name = "inference_date")
    private LocalDateTime inferenceDate;

    @Column(name = "user_id")
    private int userId;

    @Column(name = "baby_id", nullable = true)
    private Integer babyId;

  public CalendarPhotoInf(){}

    public CalendarPhotoInf(Integer calendarPhotoInfId, Integer calendarPhotoId, String inferenceResult, LocalDateTime inferenceDate, int userId, Integer babyId) {
        this.calendarPhotoInfId = calendarPhotoInfId;
        this.calendarPhotoId = calendarPhotoId;
        this.inferenceResult = inferenceResult;
        this.inferenceDate = inferenceDate;
        this.userId = userId;
        this.babyId = babyId;
    }

    public Integer getCalendarPhotoInfId() {
        return calendarPhotoInfId;
    }

    public void setCalendarPhotoInfId(Integer calendarPhotoInfId) {
        this.calendarPhotoInfId = calendarPhotoInfId;
    }

    public Integer getCalendarPhotoId() {
        return calendarPhotoId;
    }

    public void setCalendarPhotoId(Integer calendarPhotoId) {
        this.calendarPhotoId = calendarPhotoId;
    }

    public String getInferenceResult() {
        return inferenceResult;
    }

    public void setInferenceResult(String inferenceResult) {
        this.inferenceResult = inferenceResult;
    }

    public LocalDateTime getInferenceDate() {
        return inferenceDate;
    }

    public void setInferenceDate(LocalDateTime inferenceDate) {
        this.inferenceDate = inferenceDate;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Integer getBabyId() {
        return babyId;
    }

    public void setBabyId(Integer babyId) {
        this.babyId = babyId;
    }

    @Override
    public String toString() {
        return "CalendarPhotoInf{" +
                "calendarPhotoInfId=" + calendarPhotoInfId +
                ", calendarPhotoId=" + calendarPhotoId +
                ", inferenceResult='" + inferenceResult + '\'' +
                ", inferenceDate=" + inferenceDate +
                ", userId=" + userId +
                ", babyId=" + babyId +
                '}';
    }
}