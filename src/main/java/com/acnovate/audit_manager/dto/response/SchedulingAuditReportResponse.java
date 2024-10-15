package com.acnovate.audit_manager.dto.response;

import com.acnovate.audit_manager.domain.SchedulingAuditReport;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SchedulingAuditReportResponse {
    private Long id;
    private Integer reportId;
    private String frequency;
    private Integer schedulingHour;
    private Integer schedulingMinute;
    private String timeMarker;
    private List<String> recipients;

    // Constructor that accepts the SchedulingAuditReport entity
    public SchedulingAuditReportResponse(SchedulingAuditReport schedulingAuditReport) {
        this.id = schedulingAuditReport.getId();
        this.reportId = schedulingAuditReport.getReportId();
        this.frequency = schedulingAuditReport.getFrequency();
        this.schedulingHour = schedulingAuditReport.getSchedulingHour();
        this.schedulingMinute = schedulingAuditReport.getSchedulingMinute();
        this.timeMarker = schedulingAuditReport.getTimeMarker();
        // Convert the recipients string back to a list
        this.recipients = Arrays.asList(schedulingAuditReport.getRecipients().split(","));
    }
}

