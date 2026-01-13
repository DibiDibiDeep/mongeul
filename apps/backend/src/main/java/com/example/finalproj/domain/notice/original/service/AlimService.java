package com.example.finalproj.domain.notice.original.service;

import com.example.finalproj.domain.notice.inference.repository.AlimInfRepository;
import com.example.finalproj.domain.notice.original.repository.AlimRepository;
import com.example.finalproj.domain.notice.original.entity.Alim;
import com.example.finalproj.ml.alimML.AlimMLService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AlimService {
    private final AlimRepository alimRepository;
    private final AlimMLService alimMlService;
    private final AlimInfRepository alimInfRepository;

    @Autowired
    public AlimService(AlimRepository alimRepository, AlimMLService alimMlService, AlimInfRepository alimInfRepository) {
        this.alimRepository = alimRepository;
        this.alimMlService = alimMlService;
        this.alimInfRepository = alimInfRepository;
    }

    // 주어진 날짜로 Alim 목록을 조회
    public List<Alim> getAlimsByDate(LocalDate date) {
        return alimRepository.findByDate(date);
    }

    // 새로운 Alim 생성 및 ML 처리
    public Alim createAlim(Alim alim) {
        Alim savedAlim = alimRepository.save(alim);

        if (savedAlim.isSendToML()) {
            alimMlService.sendAlimToMLService(
                    savedAlim.getUserId(),
                    savedAlim.getBabyId(),
                    savedAlim.getContent(),
                    savedAlim.getAlimId()
            );
        }

        return savedAlim;
    }
    // 주어진 사용자 ID로 Alim을 조회
    public Optional<Alim> getAlimById(Integer userid) {
        return alimRepository.findById(userid);
    }

    // 모든 Alim을 조회
    public List<Alim> getAllAlims() {
        return alimRepository.findAll();
    }

    // 사용자 ID와 날짜 범위로 Alim 목록을 조회
    public List<Alim> getAlimsByUserIdAndDateRange(Integer userId, LocalDateTime start, LocalDateTime end) {
        return alimRepository.findByUserIdAndDateBetween(userId, start, end);
    }

    // Alim 수정
    public Alim updateAlim(Integer id, Alim alimDetails) {
        Optional<Alim> alimOptional = alimRepository.findById(id);

        if (alimOptional.isPresent()) {
            Alim existingAlim = alimOptional.get();

            existingAlim.setUserId(alimDetails.getUserId());
            existingAlim.setBabyId(alimDetails.getBabyId());
            existingAlim.setContent(alimDetails.getContent());
            existingAlim.setDate(alimDetails.getDate());
            existingAlim.setSendToML(alimDetails.isSendToML());

            Alim updatedAlim = alimRepository.save(existingAlim);

            if (updatedAlim.isSendToML()) {
                alimMlService.sendAlimToMLService(
                        updatedAlim.getUserId(),
                        updatedAlim.getBabyId(),
                        updatedAlim.getContent(),
                        updatedAlim.getAlimId()
                );
            }

            return updatedAlim;
        }

        return null;
    }

    // Alim 삭제
    @Transactional
    public void deleteAlim(Integer id) {
        alimInfRepository.deleteByAlimId(id);
        alimRepository.deleteById(id);
    }

    public List<Alim> getAlimsByUserIdAndBabyId(Integer userId, Integer babyId) {
        return alimRepository.findByUserIdAndBabyId(userId, babyId);
    }
}