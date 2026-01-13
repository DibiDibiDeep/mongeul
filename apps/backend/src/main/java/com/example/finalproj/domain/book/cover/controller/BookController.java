package com.example.finalproj.domain.book.cover.controller;

import com.example.finalproj.domain.book.cover.entity.Book;
import com.example.finalproj.domain.book.cover.service.BookService;
import com.example.finalproj.domain.notice.inference.entity.AlimInf;
import com.example.finalproj.domain.notice.inference.service.AlimInfService;
import com.example.finalproj.ml.bookML.BookMLService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/books")
public class BookController {

    // 로깅을 위한 Logger 객체 생성
    private static final Logger logger = LoggerFactory.getLogger(BookController.class);

    // 필요한 서비스 클래스들을 주입받음
    @Autowired
    private BookService bookService;

    @Autowired
    private AlimInfService alimInfService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BookMLService bookMLService;

    // ID로 책 조회 엔드포인트
    @GetMapping("/{id}")
    public ResponseEntity<Book> getBook(@PathVariable Integer id) {
        // 책 ID로 책을 조회하고 페이지 정보와 함께 반환
        return bookService.getBookWithPages(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 모든 책 조회 엔드포인트 (간소화된 정보)
    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        // 모든 책의 간소화된 정보를 조회하여 반환
        List<Book> books = bookService.getSimplifiedBooks();
        return ResponseEntity.ok(books);
    }

    // 책 업데이트 엔드포인트
    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable Integer id, @RequestBody Book bookDetails) {
        // 특정 ID의 책 정보를 업데이트
        Book updatedBook = bookService.updateBook(id, bookDetails);
        if (updatedBook == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedBook);
    }

    // 책 삭제 엔드포인트
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Integer id) {
        // 특정 ID의 책을 삭제
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    // 사용자 ID로 책 조회 엔드포인트
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Book>> getBooksByUserId(@PathVariable Integer userId) {
        // 특정 사용자 ID에 해당하는 모든 책을 조회
        List<Book> books = bookService.getBooksByUserId(userId);
        return ResponseEntity.ok(books);
    }

    // 동화 생성 엔드포인트 (AlimInf ID로 데이터 조회 후 ML 전송)
    @PostMapping("/generate_fairytale/{alimInfId}")
    public ResponseEntity<?> generateFairyTale(@PathVariable Integer alimInfId) {
        try {
            // AlimInf ID로 데이터를 가져옴 (Optional로 처리)
            Optional<AlimInf> optionalAlimInf = alimInfService.getAlimInfById(alimInfId);

            // AlimInf가 존재하지 않으면 에러 반환
            if (optionalAlimInf.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("해당 ID의 AlimInf 정보를 찾을 수 없습니다.");
            }

            // Optional에서 AlimInf 객체를 가져옴
            AlimInf alimInf = optionalAlimInf.get();

            // 가져온 AlimInf 데이터를 이용해 ML 모델에 전송하고 동화 생성
            String fairyTale = bookService.generateFairyTale(alimInf);

            // Base64로 인코딩된 ML 응답 로그 출력
            if (fairyTale != null) {
                logger.info("ML 서비스 응답 (Base64): {}", "동화생성 성공");
            } else {
                logger.info("ML 서비스 응답 (Base64): {}", "동화생성 실패");
            }

            // ML 응답을 기반으로 책 생성
            Book createdBook = bookService.createBookFromMLResponse(fairyTale, alimInf.getUserId(), alimInf.getBabyId());

            // 생성된 책을 응답으로 반환
            return ResponseEntity.ok(createdBook);
        } catch (Exception e) {
            logger.error("동화 생성 및 책 생성 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("동화 생성 및 책 생성 실패: " + e.getMessage());
        }
    }

    // 동화 생성 상태 조회 엔드포인트
    @GetMapping("/fairytale-status/{alimId}")
    public ResponseEntity<?> getFairyTaleStatus(@PathVariable Integer alimId) {
        String status = bookMLService.getFairyTaleStatus(alimId);
        Map<String, Object> response = new HashMap<>();
        response.put("alimId", alimId);
        response.put("status", status);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}/baby/{babyId}")
    public ResponseEntity<List<Book>> getBookByUserIdAndBabyId(
            @PathVariable Integer userId,
            @PathVariable Integer babyId) {
        List<Book> books = bookService.getBookByUserIdAndBabyId(userId, babyId);
        return ResponseEntity.ok(books);
    }
}