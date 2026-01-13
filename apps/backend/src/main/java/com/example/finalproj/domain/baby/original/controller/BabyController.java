package com.example.finalproj.domain.baby.original.controller;

import com.example.finalproj.domain.baby.original.entity.Baby;
import com.example.finalproj.domain.baby.original.service.BabyService;
import com.example.finalproj.domain.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/baby")
public class BabyController {
    // BabyService 주입
    @Autowired
    private BabyService babyService;

    @Autowired
    private UserRepository userRepository;

    // 모든 아기 정보 조회
    @GetMapping
    public ResponseEntity<List<Baby>> getAllBabies() {
        return ResponseEntity.ok(babyService.getAllBabies());
    }

    // 특정 아기 정보 조회
    @GetMapping("/{babyId}")
    public ResponseEntity<Baby> getBabyById(@PathVariable Integer babyId) {
        return babyService.getBabyById(babyId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 사용자 ID로 아기 정보 조회
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Baby>> getBabyByUserId(@PathVariable Integer userId) {
        List<Baby> babies = babyService.getBabyByUserId(userId);
        return ResponseEntity.ok(babies);
    }

    // 새로운 아기 정보 생성
    @PostMapping
    public Object createBaby(@RequestBody Baby baby) {
        if (!userRepository.existsById(baby.getUserId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("User ID does not exist.");
        }
        Baby savedBaby = babyService.save(baby);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBaby);
    }

    // 아기 정보 수정
    @PutMapping("/{babyId}")
    public ResponseEntity<Baby> updateBaby(@PathVariable Integer babyId, @RequestBody Baby babyDetails) {
        Baby updatedBaby = babyService.updateBaby(babyId, babyDetails);
        if (updatedBaby == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedBaby);
    }

    // 아기 정보 삭제
    @DeleteMapping("/{babyId}")
    public ResponseEntity<Void> deleteBaby(@PathVariable Integer babyId) {
        babyService.deleteBaby(babyId);
        return ResponseEntity.ok().build();
    }
}