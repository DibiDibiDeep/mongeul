package com.example.finalproj.domain.notice.original.controller;

import com.example.finalproj.domain.notice.original.entity.Alim;
import com.example.finalproj.domain.notice.original.service.AlimService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/alims")
public class AlimController {

    @Autowired
    private AlimService alimService;

    // 새로운 Alim 생성
    @PostMapping
    public ResponseEntity<Alim> createAlim(@RequestBody Alim alim) {
        return ResponseEntity.ok(alimService.createAlim(alim));
    }

    // userID로 Alim 조회
    @GetMapping("/{id}")
    public ResponseEntity<Alim> getAlimById(@PathVariable Integer id) {
        return alimService.getAlimById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 모든 Alim 조회
    @GetMapping
    public ResponseEntity<List<Alim>> getAllAlims() {
        return ResponseEntity.ok(alimService.getAllAlims());
    }

    // 특정 날짜의 Alim 조회
    @GetMapping("/date/{date}")
    public ResponseEntity<List<Alim>> getAlimsByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<Alim> alims = alimService.getAlimsByDate(date);
        return ResponseEntity.ok(alims);
    }

    // 특정 사용자의 특정 기간 Alim 조회
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Alim>> getAlimsByUserIdAndDateRange(
            @PathVariable Integer userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(alimService.getAlimsByUserIdAndDateRange(userId, start, end));
    }

    // Alim 업데이트
    @PutMapping("/{id}")
    public ResponseEntity<Alim> updateAlim(@PathVariable Integer id, @RequestBody Alim alimDetails) {
        Alim updatedAlim = alimService.updateAlim(id, alimDetails);
        if (updatedAlim == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedAlim);
    }

    // Alim 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlim(@PathVariable Integer id) {
        alimService.deleteAlim(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user/{userId}/baby/{babyId}")
    public ResponseEntity<List<Alim>> getAlimsByUserIdAndBabyId
            (@PathVariable Integer userId,
             @PathVariable Integer babyId) {
            List<Alim> alims = alimService.getAlimsByUserIdAndBabyId(userId, babyId);
            return ResponseEntity.ok(alims);
    }
}