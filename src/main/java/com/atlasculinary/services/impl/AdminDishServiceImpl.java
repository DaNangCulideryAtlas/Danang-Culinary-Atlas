package com.atlasculinary.services.impl;

import com.atlasculinary.dtos.DishDto;
import com.atlasculinary.dtos.UpdateDishApprovalRequest;
import com.atlasculinary.entities.Account;
import com.atlasculinary.entities.Dish;
import com.atlasculinary.enums.ApprovalStatus;
import com.atlasculinary.exceptions.InvalidRequestException;
import com.atlasculinary.exceptions.ResourceNotFoundException;
import com.atlasculinary.mappers.DishMapper;
import com.atlasculinary.repositories.AccountRepository;
import com.atlasculinary.repositories.DishRepository;
import com.atlasculinary.services.AdminDishService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AdminDishServiceImpl implements AdminDishService {
    private final DishRepository dishRepository;
    private final AccountRepository accountRepository;
    private final DishMapper dishMapper;

    @Override
    public DishDto approveOrRejectDish(UUID dishId, UpdateDishApprovalRequest request, UUID adminAccountId) {
        Dish dish = dishRepository.findById(dishId)
                .orElseThrow(()-> new ResourceNotFoundException("Dish not found with ID: " + dishId));

        ApprovalStatus requestStatus = request.getApprovalStatus();

        Account admin = accountRepository.getReferenceById(adminAccountId);
        dish.setApprovedBy(admin);
        dish.setApprovalStatus(requestStatus);
        dish.setApprovedAt(LocalDateTime.now());
        if (requestStatus == ApprovalStatus.REJECTED) {
            String rejectionReason = request.getRejectionReason();
            if (rejectionReason == null || rejectionReason.trim().isEmpty()) {
                throw new InvalidRequestException("Rejection reason is mandatory when status is REJECTED.");
            }

            dish.setRejectionReason(rejectionReason);

        } else {
            dish.setRejectionReason(null);
        }

        dishRepository.save(dish);


        return dishMapper.toDto(dish);
    }

    @Override
    public Page<DishDto> getPendingDishes(int page, int size, String sortBy, String sortDirection) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ?
                Sort.Direction.DESC :
                Sort.Direction.ASC;

        Sort sort = Sort.by(direction, sortBy);

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Dish> pendingDishesPage = dishRepository.findByApprovalStatus(ApprovalStatus.PENDING, pageable);

        return pendingDishesPage.map(dishMapper::toDto);
    }

    @Override
    public Page<DishDto> getRejectedDishes(int page, int size, String sortBy, String sortDirection) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ?
                Sort.Direction.DESC :
                Sort.Direction.ASC;

        Sort sort = Sort.by(direction, sortBy);

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Dish> rejectedDishesPage = dishRepository.findByApprovalStatus(ApprovalStatus.REJECTED, pageable);

        return rejectedDishesPage.map(dishMapper::toDto);
    }
}
