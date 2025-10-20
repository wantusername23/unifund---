package org.example.unifundemo.controller

import org.example.unifundemo.dto.notification.NotificationResponse
import org.example.unifundemo.service.NotificationService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.security.Principal

@RestController
@RequestMapping("/api/notifications")
class NotificationController(
    private val notificationService: NotificationService
) {
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