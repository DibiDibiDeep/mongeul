package com.example.finalproj.domain.notice.original.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Alim")
public class Alim {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alim_id")
    private Integer alimId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "baby_id", nullable = false)
    private Integer babyId;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "date", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime date;

    @Column(name = "send_to_ml", nullable = false)
    private boolean sendToML;

    public Alim(Integer alimId, Integer userId, Integer babyId, String content, LocalDateTime date, boolean sendToML) {
        this.alimId = alimId;
        this.userId = userId;
        this.babyId = babyId;
        this.content = content;
        this.date = date;
        this.sendToML = sendToML;
    }

    public Alim() {

    }

    public Integer getAlimId() {
        return alimId;
    }

    public void setAlimId(Integer alimId) {
        this.alimId = alimId;
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

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public boolean isSendToML() {
        return sendToML;
    }

    public void setSendToML(boolean sendToML) {
        this.sendToML = sendToML;
    }

    @Override
    public String toString() {
        return "Alim{" +
                "alimId=" + alimId +
                ", userId=" + userId +
                ", babyId=" + babyId +
                ", content='" + content + '\'' +
                ", date=" + date +
                ", sendToML=" + sendToML +
                '}';
    }
}