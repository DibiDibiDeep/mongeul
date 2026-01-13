package com.example.finalproj.ml.bookML;

import com.example.finalproj.domain.book.cover.service.BookService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class BookMLEventListener {
    // 로거 설정
    private static final Logger logger = LoggerFactory.getLogger(BookMLEventListener.class);

    // BookService 의존성 주입
    private final BookService bookService;

    // 생성자를 통한 의존성 주입
    public BookMLEventListener(BookService bookService) {
        this.bookService = bookService;
    }

    // 책 ML 처리 완료 이벤트 핸들러
    @EventListener
    public void handleBookMLProcessingCompletedEvent(BookMLProcessingCompletedEvent event) {
        String mlResponse = event.getMlResponse(); // ML 서비스로부터 받은 응답
        Integer bookId = event.getBookId(); // 처리할 책의 ID
        try {
            // BookService를 통해 ML 응답으로 책 정보 업데이트
            bookService.updateBookWithMLResponse(bookId, mlResponse);
            logger.info("책 ID: {}에 대한 ML 응답으로 업데이트 완료", bookId);
        } catch (Exception e) {
            // 오류 발생 시 로그 기록
            logger.error("책 ID: {}에 대한 ML 응답 업데이트 중 오류 발생: {}", bookId, e.getMessage(), e);
        }
    }
}