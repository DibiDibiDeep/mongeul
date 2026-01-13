package com.example.finalproj.domain.calendar.newsletter.original.controller;

import com.example.finalproj.domain.calendar.newsletter.original.entity.CalendarPhoto;
import com.example.finalproj.domain.calendar.newsletter.original.service.CalendarPhotoService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/calendar-photos")
public class CalendarPhotoController {

    private final CalendarPhotoService calendarPhotoService;

    public CalendarPhotoController(CalendarPhotoService calendarPhotoService) {
        this.calendarPhotoService = calendarPhotoService;
    }

    // 모든 CalendarPhoto 목록을 조회
    @GetMapping
    public ResponseEntity<List<CalendarPhoto>> getAllCalendarPhotos() {
        return ResponseEntity.ok(calendarPhotoService.getAllCalendarPhotos());
    }

    // 특정 ID로 CalendarPhoto 조회
    @GetMapping("/{id}")
    public ResponseEntity<CalendarPhoto> getCalendarPhotoById(@PathVariable Integer id) {
        return calendarPhotoService.getCalendarPhotoById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 특정 사용자 ID로 CalendarPhoto 목록을 조회
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CalendarPhoto>> getCalendarPhotosByUserId(@PathVariable Integer userId) {
        return ResponseEntity.ok(calendarPhotoService.getCalendarPhotosByUserId(userId));
    }

    // 특정 아기 ID로 CalendarPhoto 목록을 조회
    @GetMapping("/baby/{babyId}")
    public ResponseEntity<List<CalendarPhoto>> getCalendarPhotosByBabyId(@PathVariable Integer babyId) {
        return ResponseEntity.ok(calendarPhotoService.getCalendarPhotosByBabyId(babyId));
    }

    // 특정 날짜로 촬영된 CalendarPhoto 목록을 조회
    @GetMapping("/date/{date}")
    public ResponseEntity<List<CalendarPhoto>> getCalendarPhotosByTakenAt(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(calendarPhotoService.getCalendarPhotosByDate(date.atStartOfDay()));
    }

    // 특정 연도와 월로 CalendarPhoto 목록을 조회
    @GetMapping("/year/{year}/month/{month}")
    public ResponseEntity<List<CalendarPhoto>> getCalendarPhotosByYearAndMonth(
            @PathVariable int year,
            @PathVariable int month) {
        List<CalendarPhoto> photos = calendarPhotoService.getCalendarPhotosByYearAndMonth(year, month);
        return ResponseEntity.ok(photos);
    }

    // 새로운 CalendarPhoto 생성
    @PostMapping
    public ResponseEntity<?> createCalendarPhoto(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") Integer userId,
            @RequestParam("babyId") Integer babyId,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
        try {
            CalendarPhoto photo = calendarPhotoService.createCalendarPhoto(file, userId, babyId, date);
            return ResponseEntity.ok(photo);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("파일 업로드 중 오류 발생: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("예기치 않은 오류 발생: " + e.getMessage());
        }
    }

    // 특정 ID로 CalendarPhoto 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCalendarPhoto(@PathVariable Integer id) {
        calendarPhotoService.deleteCalendarPhoto(id);
        return ResponseEntity.ok().build();
    }
}
