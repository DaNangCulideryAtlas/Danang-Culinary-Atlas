package com.atlasculinary.services.impl;

import com.atlasculinary.dtos.AddDishRequest;
import com.atlasculinary.dtos.DishDto;
import com.atlasculinary.dtos.UpdateDishRequest;
import com.atlasculinary.dtos.UpdateDishStatusRequest;
import com.atlasculinary.entities.Dish;
import com.atlasculinary.enums.ApprovalStatus;
import com.atlasculinary.enums.DishStatus;
import com.atlasculinary.exceptions.ResourceNotFoundException;
import com.atlasculinary.mappers.DishMapper;
import com.atlasculinary.repositories.DishRepository;
import com.atlasculinary.repositories.RestaurantRepository;
import com.atlasculinary.securities.CustomAccountDetails;
import com.atlasculinary.services.AccountService;
import com.atlasculinary.services.DishService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

@AllArgsConstructor
@Service
public class DishServiceImpl implements DishService {
    private static final Logger LOGGER = Logger.getLogger(DishServiceImpl.class.getName());
    private final DishRepository dishRepository;
    private final DishMapper dishMapper;
    private final RestaurantRepository restaurantRepository;
    private final AccountService accountService;


    @Override
    public DishDto createDish(AddDishRequest request, UUID accessAccountId) {
        UUID restaurantId = request.getRestaurantId();
        var restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(()-> new ResourceNotFoundException("Restaurant not found with ID: " + restaurantId));

        UUID ownerId = restaurant.getOwnerAccount().getAccountId();
        boolean isAdmin = accountService.isAdmin(accessAccountId);

        if (!ownerId.equals(accessAccountId) && !isAdmin) {
            throw new SecurityException("Bạn không có quyền thêm món ăn.");
        }
        Dish dish = dishMapper.toEntity(request);
        dish.setRestaurant(restaurant);
        dish.setApprovalStatus(ApprovalStatus.PENDING);

        var dishSaved = dishRepository.save(dish);

        return dishMapper.toDto(dishSaved);

    }

    @Override
    public DishDto updateDish(UUID dishId, UpdateDishRequest request, UUID accessAccountId) {

        Dish dish = dishRepository.findById(dishId)
                .orElseThrow(()-> new ResourceNotFoundException("Dish not found with ID: " + dishId));

        UUID ownerId = dish.getRestaurant().getOwnerAccount().getAccountId();
        boolean isAdmin = accountService.isAdmin(accessAccountId);

        if (!ownerId.equals(accessAccountId) && !isAdmin) {
            throw new SecurityException("Bạn không có quyền chỉnh sửa thông tin món ăn này.");
        }



        dishMapper.updateDishFromRequest(request, dish);

        dishRepository.save(dish);
        return dishMapper.toDto(dish);

    }

    @Override
    public DishDto updateDishStatus(UUID dishId, UpdateDishStatusRequest request, UUID accessAccountId) {
        Dish dish = dishRepository.findById(dishId)
                .orElseThrow(()-> new ResourceNotFoundException("Dish not found with ID: " + dishId));

        UUID ownerId = dish.getRestaurant().getOwnerAccount().getAccountId();
        boolean isAdmin = accountService.isAdmin(accessAccountId);

        if (!ownerId.equals(accessAccountId) && !isAdmin) {
            throw new SecurityException("Bạn không có quyền chỉnh sửa thông tin món ăn này.");
        }

        dish.setStatus(request.getStatus());

        dishRepository.save(dish);
        return dishMapper.toDto(dish);
    }

    @Override
    public DishDto getDishById(UUID dishId) {
        // Tìm món ăn
        Dish dish = dishRepository.findById(dishId)
                .orElseThrow(() -> new ResourceNotFoundException("Món ăn không tìm thấy với ID: " + dishId));

        // Kiểm tra điều kiện công khai
        if (dish.getApprovalStatus() != ApprovalStatus.APPROVED ||
                dish.getStatus() != DishStatus.AVAILABLE) {

            // Dùng ResourceNotFoundException để ẩn sự tồn tại của món ăn nếu không công khai
            throw new ResourceNotFoundException("Món ăn không tìm thấy hoặc không khả dụng.");
        }

        return dishMapper.toDto(dish);
    }

    @Override
    public DishDto getDishDetailsForManagement(UUID dishId, CustomAccountDetails principal) {

        Dish dish = dishRepository.findById(dishId)
                .orElseThrow(() -> new ResourceNotFoundException("Món ăn không tìm thấy với ID: " + dishId));

        UUID accountId = principal.getAccountId();
        Collection<? extends GrantedAuthority> roles = principal.getAuthorities();

        boolean hasAdminRole = roles.stream().anyMatch(a -> a.getAuthority().equals("ADMIN"));
        boolean hasVendorRole = roles.stream().anyMatch(a -> a.getAuthority().equals("VENDOR"));

        if (hasAdminRole) {
            return dishMapper.toDto(dish);
        }

        if (hasVendorRole) {
            UUID ownerAccountId = dish.getRestaurant().getOwnerAccount().getAccountId();

            if (ownerAccountId.equals(accountId)) {
                return dishMapper.toDto(dish);
            }
        }
        throw new AccessDeniedException("Bạn không có quyền truy cập thông tin chi tiết món ăn này.");
    }


    @Override
    public Page<DishDto> getRestaurantDishes(UUID restaurantId, int page, int size, String sortBy, String sortDirection, UUID accessAccountId) {

        var restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(()-> new ResourceNotFoundException("Restaurant not found with ID: " + restaurantId));

        UUID ownerId = restaurant.getOwnerAccount().getAccountId();
        boolean isAdmin = accountService.isAdmin(accessAccountId);

        if (!ownerId.equals(accessAccountId) && !isAdmin) {
            throw new SecurityException("Bạn không có quyền lấy danh sách món ăn.");
        }

        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ?
                Sort.Direction.DESC :
                Sort.Direction.ASC;

        Sort sort = Sort.by(direction, sortBy);

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Dish> dishPage = dishRepository.findByRestaurant_RestaurantId(restaurantId, pageable);

        return dishPage.map(dishMapper::toDto);
    }

    @Override
    public Page<DishDto> getAvailableDishes(UUID restaurantId, int page, int size, String sortBy, String sortDirection) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ?
                Sort.Direction.DESC :
                Sort.Direction.ASC;

        Sort sort = Sort.by(direction, sortBy);

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Dish> dishPage = dishRepository.findByRestaurant_RestaurantIdAndStatusAndApprovalStatus(restaurantId,
                DishStatus.AVAILABLE,
                ApprovalStatus.APPROVED,
                pageable);

        return dishPage.map(dishMapper::toDto);
    }
}
