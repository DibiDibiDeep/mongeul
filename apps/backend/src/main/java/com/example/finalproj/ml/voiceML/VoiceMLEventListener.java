package com.example.finalproj.ml.voiceML;

import com.example.finalproj.domain.memo.original.entity.Memo;
import com.example.finalproj.domain.memo.original.service.MemoService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class VoiceMLEventListener {

    private final MemoService memoService;

    public VoiceMLEventListener(MemoService memoService) {
        this.memoService = memoService;
    }

    @EventListener
    public void handleVoiceMLProcessingCompletedEvent(VoiceMLProcessingCompletedEvent event) {
        String transcription = event.getMlResponse(); // This is now the transcription text
        Integer userId = event.getUserId();
        Integer babyId = event.getBabyId();

        Memo memo = new Memo();
        memo.setUserId(userId);
        memo.setBabyId(babyId);
        memo.setDate(LocalDateTime.now());
        memo.setContent(transcription);
        memo.setSendToML(false);

        memoService.createMemo(memo);
    }
}