package com.example.finalproj.domain.memo.original.service;

import com.example.finalproj.domain.memo.original.entity.Memo;
import com.example.finalproj.domain.memo.original.repository.MemoRepository;
import com.example.finalproj.ml.chatML.ChatMLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MemoService {

    @Autowired
    private MemoRepository memoRepository;

    @Autowired
    private ChatMLService chatMLService;

    // 새 메모 생성
    public Memo createMemo(Memo memo) {
        Memo savedMemo = memoRepository.save(memo);
        if (Boolean.TRUE.equals(savedMemo.getSendToML())) {
            chatMLService.sendMemoToMLService(savedMemo);
        }
        return savedMemo;
    }

    // ID로 메모 조회
    public Optional<Memo> getMemoById(Integer id) {
        return memoRepository.findById(id);
    }

    // 모든 메모 조회
    public List<Memo> getAllMemos() {
        return memoRepository.findAll();
    }

    // 날짜 범위로 메모 조회
    public List<Memo> getMemosByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return memoRepository.findByDateBetween(startDate, endDate);
    }

    // 메모 수정
    public Memo updateMemo(Integer id, Memo memoDetails) {
        Optional<Memo> memo = memoRepository.findById(id);
        if (memo.isPresent()) {
            Memo existingMemo = memo.get();
            existingMemo.setUserId(memoDetails.getUserId());
            existingMemo.setTodayId(memoDetails.getTodayId());
            existingMemo.setBookId(memoDetails.getBookId());
            existingMemo.setDate(memoDetails.getDate());
            existingMemo.setContent(memoDetails.getContent());
            existingMemo.setSendToML(memoDetails.getSendToML());
            Memo updatedMemo = memoRepository.save(existingMemo);
            if (Boolean.TRUE.equals(updatedMemo.getSendToML())) {
                chatMLService.sendMemoToMLService(updatedMemo);
            }
            return updatedMemo;
        }
        return null;
    }

    // 메모 삭제
    public void deleteMemo(Integer id) {
        memoRepository.deleteById(id);
    }

    // 특정 날짜의 메모 조회
    public List<Memo> getMemosByDate(LocalDate date) {
        return memoRepository.findByDate(date);
    }

    // 특정 사용자의 특정 날짜 메모 조회
    public List<Memo> getMemosByUserAndDate(Integer userId, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);
        return memoRepository.findByUserIdAndDateBetween(userId, startOfDay, endOfDay);
    }

    public List<Memo> getMemosByUserIdAndBabyId(Integer userId, Integer babyId) {
        return memoRepository.findByUserIdAndBabyId(userId, babyId);
    }
}