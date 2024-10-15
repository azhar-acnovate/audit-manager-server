package com.acnovate.audit_manager.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class SchedulingAuditReportRequest {

    @NotNull(message = "Report ID cannot be null")
    private Integer reportId;

    @NotNull(message = "Frequency cannot be null")
    private String frequency;

    @NotNull(message = "Scheduling hour cannot be null")
    private Integer schedulingHour;

    @NotNull(message = "Scheduling minute cannot be null")
    private Integer schedulingMinute;

    @NotNull(message = "Time marker cannot be null")
    private String timeMarker;

    @NotNull(message = "Recipients cannot be null")
    private List<String> recipients;
}
