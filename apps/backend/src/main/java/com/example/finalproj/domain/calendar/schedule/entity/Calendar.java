package com.example.finalproj.domain.calendar.schedule.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "calendar")
public class Calendar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer calendarId;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "baby_id", nullable = true)
    private Integer babyId;

    @Column(name = "calendar_photo_id")
    private Integer calendarPhotoId;

    @Column(name = "today_id")
    private Integer todayId;

    @Column(name = "book_id")
    private Integer bookId;

    private String title;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    private String location;

    private String target;

    private String information;

    private String notes;

    public Calendar() {}

    public Calendar(Integer calendarId, Integer userId, Integer babyId, Integer calendarPhotoId, Integer todayId, Integer bookId, String title, LocalDateTime startTime, LocalDateTime endTime, String location, String target, String information, String notes) {
        this.calendarId = calendarId;
        this.userId = userId;
        this.babyId = babyId;
        this.calendarPhotoId = calendarPhotoId;
        this.todayId = todayId;
        this.bookId = bookId;
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.target = target;
        this.information = information;
        this.notes = notes;
    }

    public Integer getCalendarId() {
        return calendarId;
    }

    public void setCalendarId(Integer calendarId) {
        this.calendarId = calendarId;
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

    public Integer getCalendarPhotoId() {
        return calendarPhotoId;
    }

    public void setCalendarPhotoId(Integer calendarPhotoId) {
        this.calendarPhotoId = calendarPhotoId;
    }

    public Integer getTodayId() {
        return todayId;
    }

    public void setTodayId(Integer todayId) {
        this.todayId = todayId;
    }

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "Calendar{" +
                "calendarId=" + calendarId +
                ", userId=" + userId +
                ", babyId=" + babyId +
                ", calendarPhotoId=" + calendarPhotoId +
                ", todayId=" + todayId +
                ", bookId=" + bookId +
                ", title='" + title + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", location='" + location + '\'' +
                ", target='" + target + '\'' +
                ", information='" + information + '\'' +
                ", notes='" + notes + '\'' +
                '}';
    }
}