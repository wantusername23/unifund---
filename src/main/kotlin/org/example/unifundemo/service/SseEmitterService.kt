package org.example.unifundemo.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.util.concurrent.ConcurrentHashMap

@Service
class SseEmitterService {

    private val logger = LoggerFactory.getLogger(SseEmitterService::class.java)
    // 사용자 이메일을 키로 SseEmitter를 저장하는 스레드 안전 맵
    private val emitters: MutableMap<String, SseEmitter> = ConcurrentHashMap()

    // 30분 타임아웃
    private val SSE_SESSION_TIMEOUT = 30 * 60 * 1000L

    fun subscribe(userEmail: String): SseEmitter {
        val emitter = SseEmitter(SSE_SESSION_TIMEOUT)
        emitters[userEmail] = emitter

        // 타임아웃 또는 완료 시 맵에서 제거
        emitter.onCompletion {
            logger.info("SSE connection completed for user: {}", userEmail)
            emitters.remove(userEmail)
        }
        emitter.onTimeout {
            logger.info("SSE connection timed out for user: {}", userEmail)
            emitter.complete()
        }
        emitter.onError {
            logger.error("SSE connection error for user: {}", userEmail, it)
            emitter.complete()
        }

        try {
            // 연결 성공 및 식별을 위한 초기 이벤트 전송
            emitter.send(SseEmitter.event().name("connect").data("SSE connection established"))
        } catch (e: Exception) {
            logger.warn("Failed to send initial connect event to user: {}", userEmail, e)
        }

        return emitter
    }

    fun send(userEmail: String, data: Any) {
        val emitter = emitters[userEmail]
        if (emitter != null) {
            try {
                // "notification"이라는 이름의 이벤트로 알림 데이터(DTO) 전송
                emitter.send(SseEmitter.event().name("notification").data(data))
            } catch (e: Exception) {
                logger.warn("Failed to send SSE event to user: {}. Removing emitter.", userEmail, e)
                // 오류 발생 시 연결 종료 및 제거
                emitter.complete()
            }
        } else {
            logger.debug("No active SSE emitter found for user: {}", userEmail)
        }
    }
}