package com.atlasculinary.services.impl;

import com.atlasculinary.dtos.ReportRequest;
import com.atlasculinary.dtos.ReportResponse;
import com.atlasculinary.entities.*;
import com.atlasculinary.enums.ReportStatus;
import com.atlasculinary.repositories.ReportRepository;
import com.atlasculinary.repositories.AccountRepository;
import com.atlasculinary.repositories.RestaurantRepository;
import com.atlasculinary.repositories.DishRepository;
import com.atlasculinary.repositories.ReviewRepository;
import com.atlasculinary.services.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    private ReportRepository reportRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private RestaurantRepository restaurantRepository;
    @Autowired
    private DishRepository dishRepository;
    @Autowired
    private ReviewRepository reviewRepository;

    @Override
    @Transactional
    public ReportResponse createReport(ReportRequest request, String reporterUsername) {
        Account reporter = accountRepository.findByEmail(reporterUsername).orElseThrow();
        Report report = new Report();
        report.setReporterAccount(reporter);
        if (request.getRestaurantId() != null) {
            Restaurant restaurant = restaurantRepository.findById(request.getRestaurantId()).orElse(null);
            report.setRestaurant(restaurant);
        }
        if (request.getDishId() != null) {
            Dish dish = dishRepository.findById(request.getDishId()).orElse(null);
            report.setDish(dish);
        }
        if (request.getReviewId() != null) {
            Review review = reviewRepository.findById(request.getReviewId()).orElse(null);
            report.setReview(review);
        }
        report.setReason(request.getReason());
        report.setStatus(ReportStatus.PENDING);
        report = reportRepository.save(report);
        return toResponse(report);
    }

    @Override
    public List<ReportResponse> getReportsByReporter(String reporterUsername) {
        Account reporter = accountRepository.findByEmail(reporterUsername).orElseThrow();
        return reportRepository.findByReporterAccount(reporter)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<ReportResponse> getAllReports() {
        return reportRepository.findAll()
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    private ReportResponse toResponse(Report report) {
        ReportResponse res = new ReportResponse();
        res.setReportId(report.getReportId());
        res.setReason(report.getReason());
        res.setStatus(report.getStatus());
        res.setCreatedAt(report.getCreatedAt());
        res.setProcessedAt(report.getProcessedAt());
        return res;
    }
}
