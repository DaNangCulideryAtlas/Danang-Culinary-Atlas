package com.atlasculinary.dtos;

public class ReportStatisticsResponse {
    private long totalReports;
    private long pendingReports;
    private long resolvedReports;
    private long rejectedReports;

    public ReportStatisticsResponse(long total, long pending, long resolved, long rejected) {
        this.totalReports = total;
        this.pendingReports = pending;
        this.resolvedReports = resolved;
        this.rejectedReports = rejected;
    }

    public long getTotalReports() { return totalReports; }
    public void setTotalReports(long totalReports) { this.totalReports = totalReports; }
    public long getPendingReports() { return pendingReports; }
    public void setPendingReports(long pendingReports) { this.pendingReports = pendingReports; }
    public long getResolvedReports() { return resolvedReports; }
    public void setResolvedReports(long resolvedReports) { this.resolvedReports = resolvedReports; }
    public long getRejectedReports() { return rejectedReports; }
    public void setRejectedReports(long rejectedReports) { this.rejectedReports = rejectedReports; }
}