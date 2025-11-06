package com.atlasculinary.services.impl;

import com.atlasculinary.entities.RestaurantStats;
import com.atlasculinary.exceptions.ResourceNotFoundException;
import com.atlasculinary.repositories.RestaurantStatsRepository;
import com.atlasculinary.services.RestaurantStatsService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.scheduling.annotation.Async;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class RestaurantStatsServiceImpl implements RestaurantStatsService {

    private final RestaurantStatsRepository statRepository;
    // Độ chính xác khi chia
    private static final int CALCULATE_SCALE = 5;
    // Độ chính xác khi hiển thị
    private static final int DISPLAY_SCALE = 1;

    @Async
    @Transactional
    public void updateStatsOnReviewEvent(UUID restaurantId, Integer oldRating, Integer newRating) {

        RestaurantStats stats = statRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("RestaurantStat not found for ID: " + restaurantId));

        Integer currentSum = stats.getSumOfRatings();
        Integer currentTotal = stats.getTotalReviews();

        if (oldRating == null && newRating != null) { // THÊM MỚI
            currentSum += newRating;
            currentTotal += 1;
        } else if (oldRating != null && newRating == null) { // XÓA
            currentSum -= oldRating;
            currentTotal -= 1;
        } else if (oldRating != null && newRating != null) { // CHỈNH SỬA
            currentSum = currentSum - oldRating + newRating;
        } else {
            return; // Không có thay đổi
        }

        if (currentTotal <= 0) {
            stats.setTotalReviews(0);
            stats.setSumOfRatings(0);
            stats.setAverageRating(BigDecimal.ZERO.setScale(DISPLAY_SCALE, RoundingMode.HALF_UP));
        } else {
            BigDecimal sumAsDecimal = new BigDecimal(currentSum);
            BigDecimal totalAsDecimal = new BigDecimal(currentTotal);

            BigDecimal newAverage = sumAsDecimal.divide(
                    totalAsDecimal, CALCULATE_SCALE, RoundingMode.HALF_UP);

            stats.setTotalReviews(currentTotal);
            stats.setSumOfRatings(currentSum);
            stats.setAverageRating(newAverage.setScale(DISPLAY_SCALE, RoundingMode.HALF_UP));
        }

        stats.setUpdatedAt(LocalDateTime.now());
        statRepository.save(stats);
    }
}