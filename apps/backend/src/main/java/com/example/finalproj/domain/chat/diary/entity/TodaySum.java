package com.example.finalproj.domain.chat.diary.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "today_sums")
public class TodaySum {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "today_id")
    private Integer todayId;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "baby_id")
    private Integer babyId;

    private String content;

    private LocalDate date;

    public TodaySum() {}

    public TodaySum(Integer todayId, Integer userId, Integer babyId, String content, LocalDate date) {
        this.todayId = todayId;
        this.userId = userId;
        this.babyId = babyId;
        this.content = content;
        this.date = date;
    }

    public Integer getTodayId() {
        return todayId;
    }

    public void setTodayId(Integer todayId) {
        this.todayId = todayId;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "TodaySum{" +
                "todayId=" + todayId +
                ", userId=" + userId +
                ", babyId=" + babyId +
                ", content='" + content + '\'' +
                ", date=" + date +
                '}';
    }
}