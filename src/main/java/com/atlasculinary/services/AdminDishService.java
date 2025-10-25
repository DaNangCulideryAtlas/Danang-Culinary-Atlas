package com.atlasculinary.services;

import com.atlasculinary.dtos.DishDto;
import com.atlasculinary.dtos.UpdateDishApprovalRequest;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface AdminDishService {
    DishDto approveOrRejectDish(UUID dishId, UpdateDishApprovalRequest request, UUID adminAccountId);

    Page<DishDto> getPendingDishes(int page, int size, String sortBy, String sortDirection);

    Page<DishDto> getRejectedDishes(int page, int size, String sortBy, String sortDirection);
}
