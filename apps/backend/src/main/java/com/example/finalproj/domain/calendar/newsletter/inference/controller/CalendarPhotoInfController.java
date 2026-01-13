package com.example.finalproj.domain.calendar.newsletter.inference.controller;

import com.example.finalproj.domain.calendar.newsletter.inference.entity.CalendarPhotoInf;
import com.example.finalproj.domain.calendar.newsletter.inference.service.CalendarPhotoInfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/calendar-photo-inf")
public class CalendarPhotoInfController {

    @Autowired
    private CalendarPhotoInfService calendarPhotoInfService;

    // 추론 결과를 저장하고 처리
    @PostMapping("/{calendarPhotoId}")
    public ResponseEntity<CalendarPhotoInf> saveAndProcessInferenceResult(
            @PathVariable Integer calendarPhotoId,
            @RequestBody String inferenceResult) {
        CalendarPhotoInf processedResult = calendarPhotoInfService.saveAndProcessInferenceResult(calendarPhotoId, inferenceResult);
        return ResponseEntity.ok(processedResult);
    }

    // 모든 CalendarPhotoInf 레코드 조회
    @GetMapping
    public ResponseEntity<List<CalendarPhotoInf>> getAllCalendarPhotoInfs() {
        List<CalendarPhotoInf> calendarPhotoInfs = calendarPhotoInfService.getAllCalendarPhotoInfs();
        return ResponseEntity.ok(calendarPhotoInfs);
    }

    // userId별로 캘린더 조회
    @GetMapping("/{userId}")
    public ResponseEntity<List<CalendarPhotoInf>> getCalendarPhotoInfByUserId(@PathVariable Integer userId) {
        return ResponseEntity.ok(calendarPhotoInfService.getCalendarPhotoInfByUserId(userId));
    }
}
