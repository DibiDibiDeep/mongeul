package com.example.finalproj.domain.book.cover.service;

import com.example.finalproj.domain.book.cover.entity.Book;
import com.example.finalproj.domain.book.cover.repository.BookRepository;
import com.example.finalproj.domain.book.page.entity.Page;
import com.example.finalproj.domain.notice.inference.entity.AlimInf;
import com.example.finalproj.ml.bookML.BookMLService;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import org.json.JSONObject;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BookService {
    // 로깅을 위한 Logger 객체 생성
    private static final Logger log = LoggerFactory.getLogger(BookService.class);

    // 필요한 서비스와 리포지토리 클래스들을 주입받음
    @Autowired
    private BookMLService bookMLService;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AmazonS3 s3Client;

    // AWS S3 버킷 이름을 설정 파일에서 가져옴
    @Value("${aws.s3.bucket}")
    private String bucketName;

    // 동화 생성 메소드
    public String generateFairyTale(AlimInf alimInf) {
        log.info("동화 생성 시작: userId={}, babyId={}", alimInf.getUserId(), alimInf.getBabyId());
        try {
            // BookMLService를 통해 ML 서비스에 요청을 보내고 응답을 받음
            String mlResponse = bookMLService.sendAlimInfToMLService(alimInf);
            if (mlResponse == null || mlResponse.isEmpty()) {
                throw new RuntimeException("ML 서비스로부터 빈 응답을 받았습니다.");
            }
            log.info("동화 생성 완료: userId={}, babyId={}", alimInf.getUserId(), alimInf.getBabyId());
            return mlResponse;
        } catch (Exception e) {
            log.error("동화 생성 중 오류 발생: userId={}, babyId={}, error={}",
                    alimInf.getUserId(), alimInf.getBabyId(), e.getMessage(), e);
            throw new RuntimeException("동화 생성에 실패했습니다: " + e.getMessage(), e);
        }
    }

    // ML 응답을 기반으로 책을 생성하는 메소드
    @Transactional
    public Book createBookFromMLResponse(String mlResponse, Integer userId, Integer babyId) throws IOException {
        JSONObject jsonObject = new JSONObject(mlResponse);
        Book book = new Book();

        // 책 제목 설정
        if (jsonObject.has("title")) {
            book.setTitle(truncateString(jsonObject.getString("title"), 255)); // Assuming title column has max 255 characters
        }
        book.setUserId(userId);
        book.setBabyId(babyId);

        // 책 표지 이미지 처리
        if (jsonObject.has("cover_illustration")) {
            String coverImageBase64 = jsonObject.getString("cover_illustration");
            String coverPath = uploadBase64ImageToS3(coverImageBase64, "covers");
            if (!coverPath.isEmpty()) {
                book.setCoverPath(coverPath);
            } else {
                log.warn("Failed to upload cover image for book: {}", book.getTitle());
            }
        }

        // 페이지 처리
        if (jsonObject.has("pages")) {
            JSONArray pages = jsonObject.getJSONArray("pages");
            for (int i = 0; i < pages.length(); i++) {
                JSONObject pageJson = pages.getJSONObject(i);
                Page page = new Page();
                page.setBook(book);
                page.setPageNum(i + 1);

                if (pageJson.has("text")) {
                    page.setText(truncateString(pageJson.getString("text"), 1000000000)); // Adjust the limit as needed
                }

                // 페이지 이미지 처리 (Base64 디코딩)
                if (pageJson.has("illustration")) {
                    String pageImageBase64 = pageJson.getString("illustration");
                    String pagePath = uploadBase64ImageToS3(pageImageBase64, "pages");
                    if (!pagePath.isEmpty()) {
                        page.setImagePath(pagePath);
                    } else {
                        log.warn("Failed to upload image for page {} of book: {}", i + 1, book.getTitle());
                    }
                }

                if (pageJson.has("illustration_prompt")) {
                    page.setIllustPrompt(truncateString(pageJson.getString("illustration_prompt"), 50000000));
                }

                book.addPage(page);
            }
        }

        // 책 저장 및 반환
        Book savedBook = bookRepository.save(book);
        log.info("Book saved successfully. Book ID: {}, Baby ID: {}", savedBook.getBookId(), savedBook.getBabyId());
        return savedBook;
    }

    private String truncateString(String input, int maxLength) {
        if (input.length() <= maxLength) {
            return input;
        }
        return input.substring(0, maxLength);
    }

    // Base64 이미지를 S3에 업로드하는 메소드
    private String uploadBase64ImageToS3(String base64Image, String folder) throws IOException {

        byte[] imageBytes = Base64.getDecoder().decode(base64Image);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);

        String fileName = folder + "/" + UUID.randomUUID() + ".png";

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(imageBytes.length);
        metadata.setContentType("image/png");

        s3Client.putObject(bucketName, fileName, inputStream, metadata);

        return s3Client.getUrl(bucketName, fileName).toString();
    }

    // ID로 책을 조회하는 메소드
    public Optional<Book> getBookById(Integer id) {
        return bookRepository.findById(id);
    }

    // 모든 책을 조회하는 메소드
    public List<Book> getAllBooks() {
        List<Book> books = bookRepository.findAll();
        // 지연 로딩된 페이지들을 초기화합니다.
        books.forEach(book -> book.getPages().size());
        return books;
    }

    // 책을 업데이트하는 메소드
    public Book updateBook(Integer id, Book bookDetails) {
        Optional<Book> book = bookRepository.findById(id);
        if (book.isPresent()) {
            Book existingBook = book.get();
            existingBook.setTitle(bookDetails.getTitle());
            existingBook.setCoverPath(bookDetails.getCoverPath());
            // 필요에 따라 다른 필드 업데이트
            return bookRepository.save(existingBook);
        }
        return null;
    }

    // 책을 삭제하는 메소드
    public void deleteBook(Integer id) {
        bookRepository.deleteById(id);
    }

    // 사용자 ID로 책을 조회하는 메소드
    public List<Book> getBooksByUserId(Integer userId) {
        return bookRepository.findByUserId(userId);
    }

    // 책 정보를 간소화하여 반환하는 메소드 (무한 재귀 참조 방지를 위해 추가)
    public List<Book> getSimplifiedBooks() {
        List<Book> books = bookRepository.findAll();
        for (Book book : books) {
            // 페이지 정보를 제거하여 간소화
            book.setPages(null);
        }
        return books;
    }

    // 특정 책의 상세 정보를 조회하는 메소드 (페이지 정보 포함)
    public Optional<Book> getBookWithPages(Integer bookId) {
        Optional<Book> bookOptional = bookRepository.findById(bookId);
        if (bookOptional.isPresent()) {
            Book book = bookOptional.get();
            // 지연 로딩된 페이지들을 초기화
            book.getPages().size();
        }
        return bookOptional;
    }

    // 특정 사용자와 아기에 대한 책 목록 조회
    public List<Book> getBookByUserIdAndBabyId(Integer userId, Integer babyId) {
        return bookRepository.findByUserIdAndBabyId(userId, babyId);
    }

    // ML 응답을 기반으로 기존 책을 업데이트하는 메소드
    @Transactional
    public Book updateBookWithMLResponse(Integer bookId, String mlResponse) throws IOException {
        // 기존 책 조회
        Book existingBook = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + bookId));

        // Base64 디코딩
        byte[] decodedBytes = Base64.getDecoder().decode(mlResponse);
        String jsonResponse = new String(decodedBytes);

        JSONObject jsonObject = new JSONObject(jsonResponse);

        // 책 제목 업데이트
        existingBook.setTitle(jsonObject.getString("title"));

        // 책 표지 이미지 업데이트
        String coverImageBase64 = jsonObject.getString("title_img_path");
        String coverPath = uploadBase64ImageToS3(coverImageBase64, "covers");
        existingBook.setCoverPath(coverPath);

        // 기존 페이지 삭제
        existingBook.getPages().clear();

        // 새 페이지 추가
        JSONArray pages = jsonObject.getJSONArray("pages");
        for (int i = 0; i < pages.length(); i++) {
            JSONObject pageJson = pages.getJSONObject(i);
            Page page = new Page();
            page.setBook(existingBook);
            page.setPageNum(i + 1);
            page.setText(pageJson.getString("text"));

            String pageImageBase64 = pageJson.getString("image_url");
            String pagePath = uploadBase64ImageToS3(pageImageBase64, "pages");
            page.setImagePath(pagePath);

            page.setIllustPrompt(pageJson.getString("illustration_prompt"));

            existingBook.addPage(page);
        }

        // 업데이트된 책 저장 및 반환
        return bookRepository.save(existingBook);
    }
}