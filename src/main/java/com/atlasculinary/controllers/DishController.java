package com.atlasculinary.controllers;

import com.atlasculinary.dtos.*;
import com.atlasculinary.securities.CustomAccountDetails;
import com.atlasculinary.services.DishService;
import com.atlasculinary.services.AdminDishService; // Đã đổi tên từ AdminDishService sang DishApprovalService
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

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
@Tag(name = "Dish Management", description = "API for managing dish data")
public class DishController {

    private final DishService dishService;
    private final AdminDishService dishApprovalService;


    // =================================================================
    // ============= ENDPOINTS CHO VENDOR (Quản lý món ăn) =============
    // =================================================================

    @Operation(summary = "Create a new dish")
    @PostMapping("/dishes")
    @PreAuthorize("hasAnyAuthority('VENDOR', 'ADMIN')")
    public ResponseEntity<DishDto> createDish(
            @Valid @RequestBody AddDishRequest request,
            @AuthenticationPrincipal CustomAccountDetails principal) {

        UUID vendorAccountId = principal.getAccountId();
        DishDto newDish = dishService.createDish(request, vendorAccountId);
        return new ResponseEntity<>(newDish, HttpStatus.CREATED);
    }


    @Operation(summary = "Update an existing dish's details")
    @PutMapping("/dishes/{dishId}")
    @PreAuthorize("hasAnyAuthority('VENDOR', 'ADMIN')")
    public ResponseEntity<DishDto> updateDish(
            @PathVariable UUID dishId,
            @Valid @RequestBody UpdateDishRequest request,
            @AuthenticationPrincipal CustomAccountDetails principal) {


        UUID vendorAccountId = principal.getAccountId();
        DishDto updatedDish = dishService.updateDish(dishId, request, vendorAccountId);
        return ResponseEntity.ok(updatedDish);
    }

    @Operation(summary = "Update status of an existing dish (AVAILABLE/OUT_OF_STOCK)")
    @PatchMapping("/dishes/{dishId}/status")
    @PreAuthorize("hasAnyAuthority('VENDOR', 'ADMIN')")
    public ResponseEntity<DishDto> updateDishStatus(
            @PathVariable UUID dishId,
            @Valid @RequestBody UpdateDishStatusRequest request,
            @AuthenticationPrincipal CustomAccountDetails principal) {

        UUID vendorAccountId = principal.getAccountId();
        DishDto updatedDish = dishService.updateDishStatus(dishId, request, vendorAccountId);
        return ResponseEntity.ok(updatedDish);
    }

    @Operation(summary = "Get all dishes of a restaurant for VENDOR (bao gồm PENDING/REJECTED)")
    @GetMapping("/restaurants/{restaurantId}/vendor-dishes")
    @PreAuthorize("hasAnyAuthority('VENDOR', 'ADMIN')")
    public ResponseEntity<Page<DishDto>> getVendorDishes(
            @PathVariable UUID restaurantId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection,
            @AuthenticationPrincipal CustomAccountDetails principal) {
        UUID accessAccountId = principal.getAccountId();
        Page<DishDto> dishes = dishService.getRestaurantDishes(restaurantId, page, size, sortBy, sortDirection, accessAccountId);
        return ResponseEntity.ok(dishes);
    }


    // =================================================================
    // ============= ENDPOINTS CHO CUSTOMER (Người dùng cuối) ==========
    // =================================================================

    @Operation(summary = "Get a dish's details (APPROVED và AVAILABLE)")
    @GetMapping("/dishes/{dishId}")
    public ResponseEntity<DishDto> getDishById(@PathVariable UUID dishId) {
        DishDto dish = dishService.getDishById(dishId);
        return ResponseEntity.ok(dish);
    }

    @Operation(summary = "Get dish details for management (All Statuses) - Cho Admin/Vendor")
    @GetMapping("/management/dishes/{dishId}")
    @PreAuthorize("hasAnyAuthority('VENDOR', 'ADMIN')")
    public ResponseEntity<DishDto> getDishDetailsForManagement(
            @PathVariable UUID dishId,
            @AuthenticationPrincipal CustomAccountDetails principal) {

        DishDto dish = dishService.getDishDetailsForManagement(dishId, principal);
        return ResponseEntity.ok(dish);
    }

    @Operation(summary = "Get all dishes of restaurant for customer (APPROVED và AVAILABLE)")
    @GetMapping("/restaurants/{restaurantId}/dishes")
    public ResponseEntity<Page<DishDto>> getAvailableDishes(
            @PathVariable UUID restaurantId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        Page<DishDto> dishes = dishService.getAvailableDishes(restaurantId, page, size, sortBy, sortDirection);
        return ResponseEntity.ok(dishes);
    }


    // =================================================================
    // =========== ENDPOINTS KIỂM DUYỆT (ADMIN) ========================
    // =================================================================

    @Operation(summary = "Admin approve or reject a dish")
    @PatchMapping("/dishes/admin/{dishId}/approval")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<DishDto> approveOrRejectDish(
            @PathVariable UUID dishId,
            @Valid @RequestBody UpdateDishApprovalRequest request,
            @AuthenticationPrincipal CustomAccountDetails principal) {

        UUID adminAccountId = principal.getAccountId();
        DishDto updatedDish = dishApprovalService.approveOrRejectDish(dishId, request, adminAccountId);
        return ResponseEntity.ok(updatedDish);
    }

    @Operation(summary = "Admin get all dishes is pending")
    @GetMapping("/dishes/admin/pending")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Page<DishDto>> getPendingDishes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "approvedAt") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        Page<DishDto> dishes = dishApprovalService.getPendingDishes(page, size, sortBy, sortDirection);
        return ResponseEntity.ok(dishes);
    }

    @Operation(summary = "Admin get all dishes is rejected")
    @GetMapping("/dishes/admin/rejected")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Page<DishDto>> getRejectedDishes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "approvedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        Page<DishDto> dishes = dishApprovalService.getRejectedDishes(page, size, sortBy, sortDirection);
        return ResponseEntity.ok(dishes);
    }
}