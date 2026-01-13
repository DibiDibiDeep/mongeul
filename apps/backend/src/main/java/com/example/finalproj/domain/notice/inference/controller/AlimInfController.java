package com.example.finalproj.domain.notice.inference.controller;

import com.example.finalproj.domain.notice.inference.entity.AlimInf;
import com.example.finalproj.domain.notice.inference.service.AlimInfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/alim-inf")
public class AlimInfController {

    private final AlimInfService alimInfService;

    @Autowired
    public AlimInfController(AlimInfService alimInfService) {
        this.alimInfService = alimInfService;
    }

    // 모든 AlimInf 레코드를 조회
    @GetMapping
    public List<AlimInf> getAllAlimInfs() {
        return alimInfService.getAllAlimInfs();
    }

    // ID로 특정 AlimInf 레코드를 조회
    @GetMapping("/{id}")
    public ResponseEntity<AlimInf> getAlimInfById(@PathVariable Integer id) {
        return alimInfService.getAlimInfById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 새로운 AlimInf 레코드를 생성
    @PostMapping
    public ResponseEntity<AlimInf> createAlimInf(@RequestBody Map<String, Object> alimInfData) {
        AlimInf createdAlimInf = alimInfService.createAlimInf(alimInfData);
        return ResponseEntity.ok(createdAlimInf);
    }

    // ID로 특정 AlimInf 레코드를 수정
    @PutMapping("/{id}")
    public ResponseEntity<AlimInf> updateAlimInf(@PathVariable Integer id, @RequestBody Map<String, Object> alimInfDetails) {
        AlimInf updatedAlimInf = alimInfService.updateAlimInf(id, alimInfDetails);
        if (updatedAlimInf == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedAlimInf);
    }

    // ID로 특정 AlimInf 레코드를 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlimInf(@PathVariable Integer id) {
        alimInfService.deleteAlimInf(id);
        return ResponseEntity.noContent().build();
    }

    // 특정 날짜 범위 내의 AlimInf 레코드들을 조회
    @GetMapping("/date-range")
    public List<AlimInf> getAlimInfsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return alimInfService.getAlimInfsByDateRange(startDate, endDate);
    }

    @GetMapping("/alim-id/{alimId}")
    public ResponseEntity<AlimInf> getAlimInfByAlimId(@PathVariable Integer alimId) {
        return alimInfService.getAlimInfByAlimId(alimId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}