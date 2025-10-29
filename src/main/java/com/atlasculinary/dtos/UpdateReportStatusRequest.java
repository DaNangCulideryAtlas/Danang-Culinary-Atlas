package com.atlasculinary.dtos;

import com.atlasculinary.enums.ReportStatus;

public class UpdateReportStatusRequest {
    private ReportStatus status;

    public ReportStatus getStatus() { return status; }
    public void setStatus(ReportStatus status) { this.status = status; }
}