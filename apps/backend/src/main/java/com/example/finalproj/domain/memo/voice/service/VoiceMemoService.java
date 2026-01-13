package com.example.finalproj.domain.memo.voice.service;

import com.example.finalproj.ml.voiceML.VoiceMLService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class VoiceMemoService {

    private final VoiceMLService voiceMLService;

    public VoiceMemoService(VoiceMLService voiceMLService) {
        this.voiceMLService = voiceMLService;
    }

    public void processVoiceMemo(MultipartFile file, Integer userId, Integer babyId) throws IOException {
        voiceMLService.sendVoiceToMLService(file, userId, babyId);
        System.out.println(String.format("음성 파일 처리: userId = %d, babyId = %d", userId, babyId));
    }
}