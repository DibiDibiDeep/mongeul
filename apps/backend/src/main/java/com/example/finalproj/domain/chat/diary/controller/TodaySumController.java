package com.example.finalproj.domain.chat.diary.controller;

import com.example.finalproj.domain.chat.diary.entity.TodaySum;
import com.example.finalproj.domain.chat.diary.service.TodaySumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/today-sum")
public class TodaySumController {

    private final TodaySumService todaySumService;

    @Autowired
    public TodaySumController(TodaySumService todaySumService) {
        this.todaySumService = todaySumService;
    }

    // 모든 TodaySum 레코드를 조회하는 GET 메소드
    @GetMapping
    public List<TodaySum> getAllTodaySums() {
        return todaySumService.getAllTodaySums();
    }

    // ID로 TodaySum 레코드를 조회하는 GET 메소드
    @GetMapping("/{id}")
    public ResponseEntity<TodaySum> getTodaySumById(@PathVariable Integer id) {
        return todaySumService.getTodaySumById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 새로운 TodaySum 레코드를 생성하는 POST 메소드
    @PostMapping
    public ResponseEntity<TodaySum> createTodaySum(@RequestBody TodaySum todaySum) {
        try {
            TodaySum createdTodaySum = todaySumService.createTodaySum(todaySum);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTodaySum);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // 기존 TodaySum 레코드를 업데이트하는 PUT 메소드
    @PutMapping("/{id}")
    public ResponseEntity<TodaySum> updateTodaySum(@PathVariable Integer id, @RequestBody TodaySum todaySumDetails) {
        TodaySum updatedTodaySum = todaySumService.updateTodaySum(id, todaySumDetails);
        if (updatedTodaySum == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedTodaySum);
    }

    // ID로 TodaySum 레코드를 삭제하는 DELETE 메소드
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodaySum(@PathVariable Integer id) {
        todaySumService.deleteTodaySum(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}/baby/{babyId}")
    public ResponseEntity<List<TodaySum>> getTodaySumByUserIdAndBabyId(
            @PathVariable Integer userId,
            @PathVariable Integer babyId) {
        List<TodaySum> todaySums = todaySumService.getTodaySumByUserIdAndBabyId(userId, babyId);
        return ResponseEntity.ok(todaySums);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TodaySum>> getTodaySumByUserId(@PathVariable Integer userId) {
        List<TodaySum> sums = todaySumService.getTodaySumByUserId(userId);
        return ResponseEntity.ok(sums);
    }
}
