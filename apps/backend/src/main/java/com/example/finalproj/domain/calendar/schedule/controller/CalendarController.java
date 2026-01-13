package com.example.finalproj.domain.calendar.schedule.controller;

import com.example.finalproj.domain.calendar.schedule.entity.Calendar;
import com.example.finalproj.domain.calendar.schedule.service.CalendarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/calendars")
public class CalendarController {
    @Autowired
    private CalendarService calendarService;

    // 모든 Calendar 레코드를 조회
    @GetMapping("/all")
    public ResponseEntity<List<Calendar>> getAllCalendars() {
        List<Calendar> calendars = calendarService.getAllCalendars();
        return ResponseEntity.ok(calendars);
    }

    // 특정 ID로 Calendar 레코드를 조회
    @GetMapping("/{calendarId}")
    public ResponseEntity<Calendar> getCalendarById(@PathVariable Integer calendarId) {
        Calendar calendar = calendarService.getCalendarById(calendarId);
        if (calendar == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(calendar);
    }

    // 특정 사용자 ID로 Calendar 목록을 조회
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Calendar>> getCalendarsByUserId(@PathVariable Integer userId) {
        return ResponseEntity.ok(calendarService.getCalendarsByUserId(userId));
    }

    // 특정 아기 ID로 Calendar 목록을 조회
    @GetMapping("/baby/{babyId}")
    public ResponseEntity<List<Calendar>> getCalendarsByBabyId(@PathVariable Integer babyId) {
        return ResponseEntity.ok(calendarService.getCalendarsByBabyId(babyId));
    }

    // 특정 사용자 ID와 아기 ID로 Calendar 목록을 조회
    @GetMapping("/user/{userId}/baby/{babyId}")
    public ResponseEntity<List<Calendar>> getCalendarsByUserIdAndBabyId(
            @PathVariable Integer userId,
            @PathVariable Integer babyId) {
        List<Calendar> calendars = calendarService.getCalendarsByUserIdAndBabyId(userId, babyId);
        return ResponseEntity.ok(calendars);
    }

    // 특정 날짜로 Calendar 목록을 조회
    @GetMapping
    public ResponseEntity<List<Calendar>> getCalendarsByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<Calendar> calendars = calendarService.getCalendarsByDate(date);
        return ResponseEntity.ok(calendars);
    }

    // 특정 날짜 범위로 Calendar 목록을 조회
    @GetMapping("/date-range")
    public List<Calendar> getCalendarsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return calendarService.getCalendarsByDateRange(startDate, endDate);
    }

    // 새로운 Calendar 레코드를 생성
    @PostMapping
    public ResponseEntity<Calendar> createCalendar(@RequestBody Calendar calendar) {
        return ResponseEntity.ok(calendarService.createCalendar(calendar));
    }

    // 특정 ID로 Calendar 레코드를 수정
    @PutMapping("/{calendarId}")
    public ResponseEntity<Calendar> updateCalendar(@PathVariable Integer calendarId, @RequestBody Calendar calendarDetails) {
        Calendar updatedCalendar = calendarService.updateCalendar(calendarId, calendarDetails);
        if (updatedCalendar == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedCalendar);
    }

    // 특정 ID로 Calendar 레코드를 삭제
    @DeleteMapping("/{calendarId}")
    public ResponseEntity<Void> deleteCalendar(@PathVariable Integer calendarId) {
        calendarService.deleteCalendar(calendarId);
        return ResponseEntity.ok().build();
    }

}
