package com.example.finalproj.domain.memo.original.controller;

import com.example.finalproj.domain.memo.original.entity.Memo;
import com.example.finalproj.domain.memo.original.service.MemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/memos")
public class MemoController {

    @Autowired
    private MemoService memoService;

    // 새 메모 생성
    @PostMapping
    public ResponseEntity<Memo> createMemo(@RequestBody Memo memo) {
        return ResponseEntity.ok(memoService.createMemo(memo));
    }

    // ID로 메모 조회
    @GetMapping("/{id}")
    public ResponseEntity<Memo> getMemoById(@PathVariable Integer id) {
        return memoService.getMemoById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 모든 메모 조회
    @GetMapping
    public ResponseEntity<List<Memo>> getAllMemos() {
        return ResponseEntity.ok(memoService.getAllMemos());
    }

    // 날짜 범위로 메모 조회
    @GetMapping("/date-range")
    public List<Memo> getMemosByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return memoService.getMemosByDateRange(startDate, endDate);
    }

    // 메모 수정
    @PutMapping("/{id}")
    public ResponseEntity<Memo> updateMemo(@PathVariable Integer id, @RequestBody Memo memoDetails) {
        Memo updatedMemo = memoService.updateMemo(id, memoDetails);
        if (updatedMemo == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedMemo);
    }

    // 메모 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMemo(@PathVariable Integer id) {
        memoService.deleteMemo(id);
        return ResponseEntity.ok().build();
    }

    // 특정 날짜의 메모 조회
    @GetMapping("/date/{date}")
    public ResponseEntity<List<Memo>> getMemosByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<Memo> memos = memoService.getMemosByDate(date);
        return ResponseEntity.ok(memos);
    }

    // 특정 사용자의 특정 날짜 메모 조회
    @GetMapping("/user/{userId}/date/{date}")
    public ResponseEntity<List<Memo>> getMemosByUserAndDate(
            @PathVariable Integer userId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<Memo> memos = memoService.getMemosByUserAndDate(userId, date);
        return ResponseEntity.ok(memos);
    }

    // 특정 사용자 ID와 아이 ID로 MEMO 조회
    @GetMapping("/user/{userId}/baby/{babyId}")
    public ResponseEntity<List<Memo>> getMemosByUserAndBaby(
            @PathVariable Integer userId,
            @PathVariable Integer babyId) {
        List<Memo> memos = memoService.getMemosByUserIdAndBabyId(userId, babyId);
        return ResponseEntity.ok(memos);
    }
}