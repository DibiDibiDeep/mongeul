package com.example.finalproj.ml.bookML;

import com.example.finalproj.domain.notice.inference.entity.AlimInf;
import com.example.finalproj.domain.baby.original.entity.Baby;
import com.example.finalproj.domain.baby.original.service.BabyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BookMLService {
    private static final Logger logger = LoggerFactory.getLogger(BookMLService.class);

    @Value("${ml.service.url}")
    private String mlServiceUrl;

    private final RestTemplate restTemplate;
    private final BabyService babyService;

    // 동화 생성 상태를 추적하기 위한 맵
    private final Map<Integer, String> fairyTaleStatus = new ConcurrentHashMap<>();

    @Autowired
    public BookMLService(RestTemplate restTemplate, BabyService babyService) {
        this.restTemplate = restTemplate;
        this.babyService = babyService;
    }

    // 동화 생성 상태를 설정하는 메소드
    public void setFairyTaleStatus(Integer alimId, String status) {
        fairyTaleStatus.put(alimId, status);
    }

    // 동화 생성 상태를 조회하는 메소드
    public String getFairyTaleStatus(Integer alimId) {
        return fairyTaleStatus.getOrDefault(alimId, "NOT_STARTED");
    }

    public String sendAlimInfToMLService(AlimInf alimInf) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = new HashMap<>();

        try {
            // 아기 정보 조회
            Baby baby = babyService.getBabyById(alimInf.getBabyId())
                    .orElseThrow(() -> new RuntimeException("Baby not found for babyId: " + alimInf.getBabyId()));

            // AlimInf 데이터 유효성 검사
            validateAlimInfData(alimInf);

            // 요청 본문 구성
            requestBody.put("name", alimInf.getName() != null ? alimInf.getName() : "");
            requestBody.put("age", baby.getBirth() != null ? calculateAge(baby.getBirth()) : null);
            requestBody.put("gender", baby.getGender() != null ? baby.getGender() : "");
            requestBody.put("emotion", alimInf.getEmotion() != null ? alimInf.getEmotion() : "");
            requestBody.put("health", alimInf.getHealth() != null ? alimInf.getHealth() : "");
            requestBody.put("nutrition", alimInf.getNutrition() != null ? alimInf.getNutrition() : "");
            requestBody.put("activities", alimInf.getActivitiesList() != null ? alimInf.getActivitiesList() : new ArrayList<>());
            requestBody.put("social", alimInf.getSocial() != null ? alimInf.getSocial() : "");
            requestBody.put("special", alimInf.getSpecial() != null ? alimInf.getSpecial() : "");
            requestBody.put("keywords", alimInf.getKeywordsList() != null ? alimInf.getKeywordsList() : new ArrayList<>());
            requestBody.put("diary", alimInf.getDiary() != null ? alimInf.getDiary() : "");
            requestBody.put("user_id", alimInf.getUserId());
            requestBody.put("baby_id", alimInf.getBabyId());

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            logger.info("ML 서비스로 요청 전송: {}", requestBody);

            // 동화 생성 상태를 "처리 중"으로 설정
            setFairyTaleStatus(alimInf.getAlimId(), "PROCESSING");

            // ML 서비스 호출
            ResponseEntity<String> response = restTemplate.postForEntity(mlServiceUrl + "/generate_fairytale", request, String.class);

            logger.info("ML 서비스로부터 응답 수신 (Base64 인코딩됨)");

            // 동화 생성 상태를 "완료"로 설정
            setFairyTaleStatus(alimInf.getAlimId(), "COMPLETED");

            // Base64로 인코딩된 응답을 그대로 반환
            return response.getBody();
        } catch (HttpClientErrorException e) {
            logger.error("ML 서비스 클라이언트 에러: {}", e.getResponseBodyAsString());
            setFairyTaleStatus(alimInf.getAlimId(), "FAILED");
            throw new RuntimeException("ML 서비스 요청 처리 중 오류 발생: " + e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException e) {
            logger.error("ML 서비스 서버 에러: {}", e.getResponseBodyAsString());
            setFairyTaleStatus(alimInf.getAlimId(), "FAILED");
            throw new RuntimeException("ML 서비스 내부 오류 발생: " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            logger.error("sendAlimInfToMLService 메소드에서 예기치 않은 오류 발생: ", e);
            setFairyTaleStatus(alimInf.getAlimId(), "FAILED");
            throw new RuntimeException("AlimInf 데이터 처리 중 예기치 않은 오류 발생: " + e.getMessage(), e);
        }
    }

    private void validateAlimInfData(AlimInf alimInf) {
        if (alimInf.getName() == null || alimInf.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        // 다른 필수 필드에 대해서도 유사한 검사 수행
    }

    private int calculateAge(LocalDateTime birthDate) {
        return Period.between(birthDate.toLocalDate(), LocalDate.now()).getYears();
    }
}