
package com.atlasculinary.services;

import com.atlasculinary.dtos.ReportRequest;
import com.atlasculinary.dtos.ReportResponse;
import com.atlasculinary.dtos.ReportStatisticsResponse;
import com.atlasculinary.enums.ReportStatus;
import java.util.List;
import java.util.UUID;

public interface ReportService {
	ReportResponse createReport(ReportRequest request, String reporterUsername);
	List<ReportResponse> getReportsByReporter(String reporterUsername);
	List<ReportResponse> getAllReports();
	ReportResponse updateReportStatus(UUID reportId, ReportStatus status, String adminUsername);
	ReportStatisticsResponse getReportStatistics();
}
