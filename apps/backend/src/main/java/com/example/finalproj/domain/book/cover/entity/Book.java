package com.example.finalproj.domain.book.cover.entity;

import com.example.finalproj.domain.book.page.entity.Page;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer bookId;
    private Integer userId;
    private Integer babyId;
    private String title;
    private String coverPath;
    private LocalDateTime generatedDate;

    // @JsonManagedReference 어노테이션을 추가하여 무한 재귀 참조를 방지합니다.
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Page> pages = new ArrayList<>();

    // 기본 생성자
    public Book() {
        this.generatedDate = LocalDateTime.now();
    }

    // bookId의 getter와 setter
    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    // userId의 getter와 setter
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    // babyId의 getter와 setter
    public Integer getBabyId() {
        return babyId;
    }

    public void setBabyId(Integer babyId) {
        this.babyId = babyId;
    }

    // title의 getter와 setter
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    // coverPath의 getter와 setter
    public String getCoverPath() {
        return coverPath;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }

    // generatedDate의 getter와 setter
    public LocalDateTime getGeneratedDate() {
        return generatedDate;
    }

    public void setGeneratedDate(LocalDateTime generatedDate) {
        this.generatedDate = generatedDate;
    }

    // pages의 getter와 setter
    public List<Page> getPages() {
        return pages;
    }

    public void setPages(List<Page> pages) {
        this.pages = pages;
    }

    // 페이지 추가 메서드
    public void addPage(Page page) {
        pages.add(page);
        page.setBook(this);
    }

    // 페이지 제거 메서드
    public void removePage(Page page) {
        pages.remove(page);
        page.setBook(null);
    }
}