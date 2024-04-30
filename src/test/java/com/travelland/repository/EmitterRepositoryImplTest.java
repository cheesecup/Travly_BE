package com.travelland.repository;

import com.travelland.repository.notification.EmitterRepository;
import com.travelland.repository.notification.EmitterRepositoryImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Optional;

class EmitterRepositoryImplTest {

    private EmitterRepository emitterRepository = new EmitterRepositoryImpl();
    private Long DEFAULT_TIMEOUT = 60L * 1000L * 60L;

    @Test
    @DisplayName("새로운 Emitter를 추가한다.")
    public void save() throws Exception {
        //given
        Long memberId = 1L;
        String emitterId =  memberId + "_" + System.currentTimeMillis();
        SseEmitter sseEmitter = new SseEmitter(DEFAULT_TIMEOUT);

        //when, then
        Assertions.assertDoesNotThrow(() -> emitterRepository.save(emitterId, sseEmitter));
    }

    @Test
    @DisplayName("어떤 회원이 접속한 모든 Emitter를 찾는다")
    public void findAllEmitterStartWithByMemberId() throws Exception {
        //given
        Long memberId = 1L;
        String emitterId1 = memberId + "_" + System.currentTimeMillis();
        emitterRepository.save(emitterId1, new SseEmitter(DEFAULT_TIMEOUT));

        //when
        Optional<SseEmitter> ActualResult = emitterRepository.findEmitterById(String.valueOf(memberId));

        //then
        Assertions.assertTrue(ActualResult.isPresent());
    }

    @Test
    @DisplayName("ID를 통해 Emitter를 Repository에서 제거한다.")
    public void deleteById() throws Exception {
        //given
        Long memberId = 1L;
        String emitterId =  memberId + "_" + System.currentTimeMillis();
        SseEmitter sseEmitter = new SseEmitter(DEFAULT_TIMEOUT);

        //when
        emitterRepository.save(emitterId, sseEmitter);
        emitterRepository.deleteById(emitterId);

        //then
        Assertions.assertFalse(emitterRepository.findEmitterById(emitterId).isPresent());
    }
}