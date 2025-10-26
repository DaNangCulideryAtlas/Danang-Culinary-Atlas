package com.atlasculinary.controllers;

import com.atlasculinary.dtos.AddRestaurantRequest;
import com.atlasculinary.dtos.RestaurantDto;
import com.atlasculinary.dtos.UpdateApprovalStatusRequest;
import com.atlasculinary.dtos.UpdateRestaurantRequest;
import com.atlasculinary.securities.CustomAccountDetails;
import com.atlasculinary.services.RestaurantService;
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

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
@Tag(name = "Restaurant Management", description = "API for managing restaurant data")
public class RestaurantController {

    private final RestaurantService restaurantService;


    // =================================================================
    // === VENDOR/ADMIN ENDPOINTS (CRUD) ===
    // =================================================================

    @Operation(summary = "Create a new restaurant (by Vendor or Admin)")
    @PostMapping("/restaurants")
    @PreAuthorize("hasAnyAuthority('VENDOR', 'ADMIN')")
    ResponseEntity<RestaurantDto> createRestaurant(@Valid @RequestBody AddRestaurantRequest addRestaurantRequest,
                                                   @AuthenticationPrincipal CustomAccountDetails principal) {
        var ownerAccountId = principal.getAccountId();
        // Service sẽ gán principal.accountId làm Owner của nhà hàng
        var restaurantDto = restaurantService.createRestaurant(ownerAccountId, addRestaurantRequest);
        return new ResponseEntity<>(restaurantDto,HttpStatus.CREATED);
    }

    @Operation(summary = "Update an existing restaurant's details")
    @PatchMapping("/restaurants/{restaurantId}")
    @PreAuthorize("hasAnyAuthority('VENDOR', 'ADMIN')")
    ResponseEntity<RestaurantDto> updateRestaurant(@PathVariable UUID restaurantId,
                                                   @Valid @RequestBody UpdateRestaurantRequest updateRestaurantRequest,
                                                   @AuthenticationPrincipal CustomAccountDetails principal) {
        var accessAccountId = principal.getAccountId();
        // Service kiểm tra accessAccountId có quyền sở hữu/Admin
        var restaurantDto = restaurantService.updateRestaurant(restaurantId, updateRestaurantRequest, accessAccountId);
        return ResponseEntity.ok(restaurantDto);
    }

    @Operation(summary = "Delete a restaurant by Id (Soft Delete, chỉ Owner hoặc Admin)")
    @DeleteMapping("/restaurants/{restaurantId}")
    @PreAuthorize("hasAnyAuthority('VENDOR', 'ADMIN')")
    ResponseEntity<Void> deleteRestaurant(@PathVariable UUID restaurantId,
                                          @AuthenticationPrincipal CustomAccountDetails principal) {
        var accessAccountId = principal.getAccountId();
        // Cải tiến: Truyền accessAccountId vào Service để kiểm tra quyền sở hữu/Admin
        restaurantService.deleteRestaurant(restaurantId, accessAccountId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // =================================================================
    // === PUBLIC ENDPOINTS (READ) ===
    // =================================================================

    @Operation(summary = "Get restaurant details by Id (Public)")
    @GetMapping("/restaurants/{restaurantId}")
    ResponseEntity<RestaurantDto> getRestaurantById(@PathVariable UUID restaurantId) {
        // Service chỉ trả về nhà hàng đã APPROVED và không bị xóa
        var restaurantDto = restaurantService.getRestaurantById(restaurantId);
        return ResponseEntity.ok(restaurantDto);
    }

    @Operation(summary = "Get all approved restaurants with pagination and sorting")
    @GetMapping("/restaurants")
    public ResponseEntity<Page<RestaurantDto>> getAllRestaurants(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        // Service chỉ trả về các nhà hàng đã APPROVED
        Page<RestaurantDto> restaurantsPage = restaurantService.getAllRestaurantsApproved(page, size, sortBy, sortDirection);
        return ResponseEntity.ok(restaurantsPage);
    }

    // =================================================================
    // === VENDOR OWNERSHIP ENDPOINTS ===
    // =================================================================

    @Operation(summary = "Get all restaurants belonging to a specific Vendor (dành cho Vendor/Admin)")
    @GetMapping("/vendors/{vendorId}/restaurants") // URI: /api/v1/vendors/{vendorId}/restaurants
    @PreAuthorize("hasAuthority('ADMIN') or #vendorId == principal.accountId")
    public ResponseEntity<Page<RestaurantDto>> getAllRestaurantsByVendor(
            @PathVariable UUID vendorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection,
            @AuthenticationPrincipal CustomAccountDetails principal
    ) {
        // Service sẽ trả về danh sách đầy đủ (bao gồm PENDING, REJECTED) cho Vendor hoặc Admin
        Page<RestaurantDto> restaurantsPage = restaurantService.getAllRestaurantsByVendor(vendorId, page, size, sortBy, sortDirection);
        return ResponseEntity.ok(restaurantsPage);
    }


    // =================================================================
    // === ADMIN ENDPOINTS (APPROVAL) ===
    // =================================================================

    @Operation(summary = "Update the approval status of a restaurant (Admin only)")
    @PatchMapping("/restaurants/admin/{restaurantId}/approval") // URI: /api/v1/admin/restaurants/{id}/approval
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<RestaurantDto> updateApprovalStatus(
            @PathVariable UUID restaurantId,
            @Valid @RequestBody UpdateApprovalStatusRequest request,
            @AuthenticationPrincipal CustomAccountDetails principal) {

        var adminAccountId = principal.getAccountId();
        RestaurantDto updatedRestaurant = restaurantService.updateApprovalStatus(adminAccountId, restaurantId, request);
        return ResponseEntity.ok(updatedRestaurant);
    }

    @Operation(summary = "ADMIN: Get all restaurants with any status (PENDING, APPROVED, REJECTED)")
    @GetMapping("/restaurants/admin") // URI: /api/v1/restaurants/admin
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Page<RestaurantDto>> getAllRestaurantsForAdmin(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        // Service này sẽ không lọc theo trạng thái APPROVED
        Page<RestaurantDto> restaurantsPage = restaurantService.getAllRestaurants(page, size, sortBy, sortDirection);
        return ResponseEntity.ok(restaurantsPage);
    }
}