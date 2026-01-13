package com.example.finalproj.domain.chat.context.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat_messages")
public class ChatMessageDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long  userId;

    @Column(nullable = false)
    private Long  babyId;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
    private String sender;

    private String sessionId;

    public ChatMessageDTO() {}

    public ChatMessageDTO(Long  userId, Long  babyId, LocalDateTime timestamp, String content, String sender, String role, String sessionId) {
        this.userId = userId;
        this.babyId = babyId;
        this.timestamp = timestamp;
        this.content = content;
        this.sender = sender;
        this.role = role;
        this.sessionId = sessionId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long  getUserId() {
        return userId;
    }

    public void setUserId(Long  userId) {
        this.userId = userId;
    }

    public Long  getBabyId() {
        return babyId;
    }

    public void setBabyId(Long babyId) {
        this.babyId = babyId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public String toString() {
        return "ChatMessageDTO{" +
                "userId='" + userId + '\'' +
                ", babyId='" + babyId + '\'' +
                ", timestamp=" + timestamp +
                ", content='" + content + '\'' +
                ", sender='" + sender + '\'' +
                ", role='" + role + '\'' +
                ", sessionId='" + sessionId + '\'' +
                '}';
    }
}
