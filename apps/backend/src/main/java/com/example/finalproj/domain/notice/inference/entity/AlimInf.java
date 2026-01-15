package com.example.finalproj.domain.notice.inference.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@Table(name = "alim_infs")
public class AlimInf {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "aliminf_id")
    private Integer alimInfId;

    @Column(name = "alim_id")
    private Integer alimId;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "baby_id")
    private Integer babyId;

    @Column(name = "today_id")
    private Integer todayId;

    @Column(name = "name", nullable = false)
    private String name;

    private String emotion;
    private String health;
    private String nutrition;

    @Column(columnDefinition = "TEXT")
    private String activities;

    private String social;
    private String special;

    @Column(columnDefinition = "TEXT")
    private String keywords;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String diary;

    @Column(name = "date")
    private LocalDateTime date;

    private String role;

    public AlimInf() {
    }

    public AlimInf(Integer alimInfId, Integer alimId, Integer userId, Integer babyId, Integer todayId, String name, String emotion, String health, String nutrition, String activities, String social, String special, String keywords, String diary, LocalDateTime date, String role) {

        this.alimInfId = alimInfId;
        this.alimId = alimId;
        this.userId = userId;
        this.babyId = babyId;
        this.todayId = todayId;
        this.name = name;
        this.emotion = emotion;
        this.health = health;
        this.nutrition = nutrition;
        this.activities = activities;
        this.social = social;
        this.special = special;
        this.keywords = keywords;
        this.diary = diary;
        this.date = date;
        this.role = role;
    }

    // Getter와 Setter 메소드들

    public Integer getAlimInfId() {
        return alimInfId;
    }

    public void setAlimInfId(Integer alimInfId) {
        this.alimInfId = alimInfId;
    }

    public Integer getAlimId() {
        return alimId;
    }

    public void setAlimId(Integer alimId) {
        this.alimId = alimId;
    }

    // userId에 대한 Getter 메소드 (null 체크 추가)
    public Integer getUserId() {
        return userId != null ? userId : 0; // userId가 null이면 0을 반환
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    // babyId에 대한 Getter 메소드 (null 체크 추가)
    public Integer getBabyId() {
        return babyId != null ? babyId : 0; // babyId가 null이면 0을 반환
    }

    public void setBabyId(Integer babyId) {
        this.babyId = babyId;
    }

    public Integer getTodayId() {
        return todayId;
    }

    public void setTodayId(Integer todayId) {
        this.todayId = todayId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmotion() {
        return emotion;
    }

    public void setEmotion(String emotion) {
        this.emotion = emotion;
    }

    public String getHealth() {
        return health;
    }

    public void setHealth(String health) {
        this.health = health;
    }

    public String getNutrition() {
        return nutrition;
    }

    public void setNutrition(String nutrition) {
        this.nutrition = nutrition;
    }

    public String getActivities() {
        return activities;
    }

    public void setActivities(String activities) {
        this.activities = activities;
    }

    // 활동 목록을 List<String>으로 반환하는 메소드
    public List<String> getActivitiesList() {
        if (activities == null || activities.isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.asList(activities.split(","));
    }

    // List<String>을 받아 문자열로 설정하는 메소드
    public void setActivitiesList(List<String> activitiesList) {
        this.activities = String.join(",", activitiesList);
    }

    public String getSocial() {
        return social;
    }

    public void setSocial(String social) {
        this.social = social;
    }

    public String getSpecial() {
        return special;
    }

    public void setSpecial(String special) {
        this.special = special;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    // 키워드 목록을 List<String>으로 반환하는 메소드
    public List<String> getKeywordsList() {
        if (keywords == null || keywords.isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.asList(keywords.split(","));
    }

    // List<String>을 받아 문자열로 설정하는 메소드
    public void setKeywordsList(List<String> keywordsList) {
        this.keywords = String.join(",", keywordsList);
    }

    public String getDiary() {
        return diary;
    }

    public void setDiary(String diary) {
        this.diary = diary;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "AlimInf{" +
                "alimInfId=" + alimInfId +
                ", alimId=" + alimId +
                ", userId=" + userId +
                ", babyId=" + babyId +
                ", todayId=" + todayId +
                ", name='" + name + '\'' +
                ", emotion='" + emotion + '\'' +
                ", health='" + health + '\'' +
                ", nutrition='" + nutrition + '\'' +
                ", activities='" + activities + '\'' +
                ", social='" + social + '\'' +
                ", special='" + special + '\'' +
                ", keywords='" + keywords + '\'' +
                ", diary='" + diary + '\'' +
                ", date=" + date +
                ", role='" + role + '\'' +
                '}';
    }
}