
package com.atlasculinary.services;

import com.atlasculinary.dtos.ReportRequest;
import com.atlasculinary.dtos.ReportResponse;
import java.util.List;

public interface ReportService {
	ReportResponse createReport(ReportRequest request, String reporterUsername);
	List<ReportResponse> getReportsByReporter(String reporterUsername);
	List<ReportResponse> getAllReports();
}
