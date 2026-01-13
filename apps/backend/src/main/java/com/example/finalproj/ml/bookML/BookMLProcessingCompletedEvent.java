package com.example.finalproj.ml.bookML;

import org.springframework.context.ApplicationEvent;

// 책 ML 처리 완료 이벤트 클래스
public class BookMLProcessingCompletedEvent extends ApplicationEvent {
    private final String mlResponse; // ML 서비스의 응답
    private final Integer bookId; // 처리된 책의 ID

    // 생성자
    public BookMLProcessingCompletedEvent(Object source, String mlResponse, Integer bookId) {
        super(source);
        this.mlResponse = mlResponse;
        this.bookId = bookId;
    }

    // ML 응답 getter
    public String getMlResponse() {
        return mlResponse;
    }

    // 책 ID getter
    public Integer getBookId() {
        return bookId;
    }
}