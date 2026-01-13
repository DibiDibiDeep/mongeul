package com.example.finalproj.domain.book.page.entity;

import com.example.finalproj.domain.book.cover.entity.Book;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "Page")
public class Page {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer pageId;

    // @JsonBackReference 어노테이션을 추가하여 무한 재귀 참조를 방지합니다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    @JsonBackReference
    private Book book;

    private Integer pageNum;

    @Column(length = 1000)
    private String text;

    private String imagePath;

    @Column(length = 1000)
    private String illustPrompt;

    // 기본 생성자
    public Page() {}

    // getter와 setter 메서드들
    public Integer getPageId() {
        return pageId;
    }

    public void setPageId(Integer pageId) {
        this.pageId = pageId;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getIllustPrompt() {
        return illustPrompt;
    }

    public void setIllustPrompt(String illustPrompt) {
        this.illustPrompt = illustPrompt;
    }
}