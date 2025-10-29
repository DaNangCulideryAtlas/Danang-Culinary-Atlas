package com.atlasculinary.controllers;

import com.atlasculinary.dtos.ReportRequest;
import com.atlasculinary.dtos.ReportResponse;
import com.atlasculinary.dtos.ReportStatisticsResponse;
import com.atlasculinary.dtos.UpdateReportStatusRequest;
import com.atlasculinary.services.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reports")
public class ReportController {
    @Autowired
    private ReportService reportService;

    @PostMapping({"", "/"})
    public ResponseEntity<ReportResponse> createReport(@RequestBody ReportRequest request, Authentication authentication) {
        String username = authentication.getName();
        ReportResponse response = reportService.createReport(request, username);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my")
    public ResponseEntity<List<ReportResponse>> getMyReports(Authentication authentication) {
        String username = authentication.getName();
        List<ReportResponse> reports = reportService.getReportsByReporter(username);
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<ReportResponse>> getAllReports() {
        List<ReportResponse> reports = reportService.getAllReports();
        return ResponseEntity.ok(reports);
    }

    @PutMapping("/admin/{reportId}/status")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ReportResponse> updateReportStatus(@PathVariable UUID reportId, @RequestBody UpdateReportStatusRequest request, Authentication authentication) {
        String adminUsername = authentication.getName();
        ReportResponse response = reportService.updateReportStatus(reportId, request.getStatus(), adminUsername);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin/statistics")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ReportStatisticsResponse> getReportStatistics() {
        ReportStatisticsResponse stats = reportService.getReportStatistics();
        return ResponseEntity.ok(stats);
    }
}
