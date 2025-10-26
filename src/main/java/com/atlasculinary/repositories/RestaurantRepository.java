package com.atlasculinary.repositories;

import com.atlasculinary.entities.Restaurant;
import com.atlasculinary.enums.ApprovalStatus;
import com.atlasculinary.enums.RestaurantStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, UUID> {
    Page<Restaurant> findByOwnerAccount_AccountId(UUID vendorId, Pageable pageable);

    Page<Restaurant> findAllByStatusAndApprovalStatus(Pageable pageable, RestaurantStatus restaurantStatus, ApprovalStatus approvalStatus);
}
