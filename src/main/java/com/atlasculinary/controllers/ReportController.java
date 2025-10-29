package com.atlasculinary.controllers;

import com.atlasculinary.dtos.ReportRequest;
import com.atlasculinary.dtos.ReportResponse;
import com.atlasculinary.services.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<List<ReportResponse>> getAllReports() {
        List<ReportResponse> reports = reportService.getAllReports();
        return ResponseEntity.ok(reports);
    }
}
