package org.example.unifundemo.controller

import org.example.unifundemo.dto.notification.NotificationResponse
import org.example.unifundemo.service.NotificationService
import org.example.unifundemo.service.SseEmitterService
import org.springframework.http.MediaType // ✅ Import 추가
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter // ✅ Import 추가
import java.security.Principal

@RestController
@RequestMapping("/api/notifications")
class NotificationController(
    private val notificationService: NotificationService,
    private val sseEmitterService: SseEmitterService
) {
    // ✅ SSE 구독 엔드포인트 추가
    @GetMapping("/subscribe", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun subscribe(principal: Principal): SseEmitter {
        return sseEmitterService.subscribe(principal.name)
    }

    @GetMapping
    fun getMyNotifications(principal: Principal): ResponseEntity<List<NotificationResponse>> {
        val notifications = notificationService.getMyNotifications(principal.name)
        return ResponseEntity.ok(notifications)
    }

    @PatchMapping("/{id}/read")
    fun markAsRead(@PathVariable id: Long, principal: Principal): ResponseEntity<Unit> {
        notificationService.markAsRead(id, principal.name)
        return ResponseEntity.ok().build()
    }
}