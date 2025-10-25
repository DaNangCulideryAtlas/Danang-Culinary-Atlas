package com.atlasculinary.controllers;

import com.atlasculinary.dtos.AddNotificationRequest;
import com.atlasculinary.dtos.NotificationDto;
import com.atlasculinary.securities.CustomAccountDetails;
import com.atlasculinary.services.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@AllArgsConstructor
@Tag(name = "Notification Management", description = "API for managing notification data")
public class NotificationController {

    private final NotificationService notificationService;


    @Operation(summary = "Get list of notifications for the current user")
    @GetMapping // URI: /api/v1/notifications
    @PreAuthorize("isAuthenticated()")
    ResponseEntity<Page<NotificationDto>> getMyNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection,
            @AuthenticationPrincipal CustomAccountDetails principal) {

        UUID accountId = principal.getAccountId();
        Page<NotificationDto> notificationDtoPage = notificationService.getNotificationsByRecipientId(accountId, page, size, sortBy, sortDirection);

        return ResponseEntity.ok(notificationDtoPage);
    }

    @Operation(summary = "Mask a specific notification as read for current user")
    @PatchMapping("/{notificationId}/read")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> markAsRead(@PathVariable Long notificationId,
                                           @AuthenticationPrincipal CustomAccountDetails principal) {
        var accessAccountId = principal.getAccountId();
        notificationService.markAsRead(accessAccountId, notificationId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Mask ALL notifications as read for current user")
    @PatchMapping("/mark-all-read") // URI: /api/v1/notifications/mark-all-read
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> markAllAsRead(@AuthenticationPrincipal CustomAccountDetails principal) {
        var accessAccountId = principal.getAccountId();
        notificationService.markAllAsRead(accessAccountId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Count unread notification for current user")
    @GetMapping("/unread/count") // URI: /api/v1/notifications/unread/count
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Long> getUnreadCount(@AuthenticationPrincipal CustomAccountDetails principal) {
        var accessAccountId = principal.getAccountId();
        Long count = notificationService.getUnreadCount(accessAccountId);
        return ResponseEntity.ok(count);
    }

    @Operation(summary = "Get top 10 unread notification for current user")
    @GetMapping("/unread/top10") // URI: /api/v1/notifications/unread/top10
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<NotificationDto>> getTop10Unread(@AuthenticationPrincipal CustomAccountDetails principal) {
        var accessAccountId = principal.getAccountId();
        List<NotificationDto> notifications = notificationService.getTop10Unread(accessAccountId);
        return ResponseEntity.ok(notifications);
    }


    @Operation(summary = "ADMIN: Get list of notifications by recipient Id")
    @GetMapping("/admin/{accountId}") // URI: /api/v1/notifications/admin/{accountId}
    @PreAuthorize("hasAuthority('ADMIN')")
    ResponseEntity<Page<NotificationDto>> getNotificationsByAdmin(
            @PathVariable UUID accountId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        Page<NotificationDto> notificationDtoPage = notificationService.getNotificationsByRecipientId(accountId, page, size, sortBy, sortDirection);
        return ResponseEntity.ok(notificationDtoPage);
    }

    @Operation(summary = "ADMIN: Count unread notification for any account")
    @GetMapping("/admin/{accountId}/unread/count") // URI: /api/v1/notifications/admin/{id}/unread/count
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Long> getUnreadCountByAdmin(@PathVariable UUID accountId) {
        Long count = notificationService.getUnreadCount(accountId);
        return ResponseEntity.ok(count);
    }

    @Operation(summary = "ADMIN: Get top 10 unread notification for any account")
    @GetMapping("/admin/{accountId}/unread/top10") // URI: /api/v1/notifications/admin/{id}/unread/top10
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<NotificationDto>> getTop10UnreadByAdmin(@PathVariable UUID accountId) {
        List<NotificationDto> notifications = notificationService.getTop10Unread(accountId);
        return ResponseEntity.ok(notifications);
    }

    @Operation(summary = "ADMIN: Mark all notification as read for any account")
    @PatchMapping("/admin/{accountId}/mark-all-read") // URI: /api/v1/notifications/admin/{id}/mark-all-read
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> markAllAsReadByAdmin(@PathVariable UUID accountId) {
        notificationService.markAllAsRead(accountId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "ADMIN: Delete a notification by ID (CRUD)")
    @DeleteMapping("/admin/{notificationId}") // URI: /api/v1/notifications/admin/{id}
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long notificationId) {
        notificationService.deleteNotification(notificationId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "ADMIN: Create and send a specific in-app notification to any user")
    @PostMapping("/admin") // URI: /api/v1/notifications/admin
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> createInAppNotification(
            @Valid @RequestBody AddNotificationRequest addNotificationRequest) {

        notificationService.createInAppNotification(addNotificationRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}